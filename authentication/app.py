import os
import logging
from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy
from werkzeug.security import generate_password_hash, check_password_hash
import jwt
import datetime

app = Flask(__name__)
app.config['SECRET_KEY'] = os.getenv('SECRET_KEY', 'your_secret_key')
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False  # Suppress FSADeprecationWarning
db_user = os.getenv('POSTGRES_USER', 'mobylab-app')
db_password = os.getenv('POSTGRES_PASSWORD', 'mobylab-app')
db_host = 'db'
db_name = 'mobylab-app'
app.config['SQLALCHEMY_DATABASE_URI'] = f'postgresql://{db_user}:{db_password}@{db_host}:5432/{db_name}'
db = SQLAlchemy(app)

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class Role(db.Model):
    __tablename__ = 'roles'  # Match the table name used by the Java server
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(50), nullable=False)
    user_id = db.Column(db.Integer, db.ForeignKey('users.id'), nullable=False)  # Correct foreign key reference

class User(db.Model):
    __tablename__ = 'users'  # Match the table name used by the Java server
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(50), unique=True, nullable=False)
    password = db.Column(db.String(200), nullable=False)
    roles = db.relationship('Role', backref='user', lazy=True)

@app.route('/register', methods=['POST'])
def register():
    data = request.get_json()
    if not data or not data.get('username') or not data.get('password'):
        logger.warning('Username and password required for registration')
        return jsonify({'message': 'Username and password required'}), 400

    if User.query.filter_by(username=data['username']).first():
        logger.warning('Username already taken: %s', data['username'])
        return jsonify({'message': 'Username already taken'}), 400

    hashed_password = generate_password_hash(data['password'], method='sha256')
    new_user = User(username=data['username'], password=hashed_password)
    db.session.add(new_user)
    db.session.commit()
    default_role = Role(name='USER', user_id=new_user.id)
    db.session.add(default_role)
    db.session.commit()

    logger.info('User registered successfully: %s', data['username'])
    return jsonify({'message': 'User registered successfully'}), 200

@app.route('/login', methods=['POST'])
def login():
    data = request.get_json()
    if not data or not data.get('username') or not data.get('password'):
        logger.warning('Username and password required for login')
        return jsonify({'message': 'Username and password required'}), 400

    user = User.query.filter_by(username=data['username']).first()
    if not user or not check_password_hash(user.password, data['password']):  # Check the password directly
        logger.warning('Invalid login attempt for username: %s', data['username'])
        return jsonify({'message': 'Invalid credentials'}), 401

    roles = [role.name for role in user.roles]
    token = jwt.encode({'username': user.username, 'roles': roles, 'exp': datetime.datetime.utcnow() + datetime.timedelta(hours=1)}, app.config['SECRET_KEY'])
    logger.info('User logged in successfully: %s', data['username'])
    return jsonify({'token': token}), 200  # Return the token directly

@app.route('/validateToken', methods=['POST'])
def validate_token():
    token = request.get_json().get('token')
    if not token:
        logger.warning('Token is missing in validateToken request')
        return jsonify({'message': 'Token is missing'}), 400

    try:
        data = jwt.decode(token, app.config['SECRET_KEY'], algorithms=["HS256"])
        roles = data.get('roles', [])
        logger.info('Token validated successfully for username: %s', data['username'])
        return jsonify({'username': data['username'], 'roles': roles}), 200
    except jwt.ExpiredSignatureError:
        logger.warning('Token has expired')
        return jsonify({'message': 'Token has expired'}), 401
    except jwt.InvalidTokenError:
        logger.warning('Invalid token')
        return jsonify({'message': 'Invalid token'}), 401

def create_default_user():
    if not User.query.filter_by(username='user').first():
        hashed_password = generate_password_hash('user', method='sha256')
        default_user = User(username='user', password=hashed_password)
        db.session.add(default_user)
        db.session.commit()
        default_role = Role(name='USER', user_id=default_user.id)
        db.session.add(default_role)
        db.session.commit()
        logger.info('Default user created: user with USER')
    else:
        # update the password if the default user already exists
        default_user = User.query.filter_by(username='user').first()
        hashed_password = generate_password_hash('user', method='sha256')

    if not User.query.filter_by(username='admin').first():
        hashed_password = generate_password_hash('admin', method='sha256')
        default_admin = User(username='admin', password=hashed_password)
        db.session.add(default_admin)
        db.session.commit()
        admin_role = Role(name='ADMIN', user_id=default_admin.id)
        user_role = Role(name='USER', user_id=default_admin.id)  # Add USER role to admin
        db.session.add(admin_role)
        db.session.add(user_role)
        db.session.commit()
        logger.info('Default admin created: admin with ADMIN and USER roles')
    else:
        # update the password if the default admin already exists
        default_admin = User.query.filter_by(username='admin').first()
        hashed_password = generate_password_hash('admin', method='sha256')

if __name__ == '__main__':
    db.create_all()  # Uncomment this line to create tables
    create_default_user()  # Uncomment this line to create the default user if it does not exist
    app.run(debug=True, host='0.0.0.0', port=5000)