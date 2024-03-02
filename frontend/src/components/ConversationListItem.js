import { Card, Button } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';

export default function ConversationListItem({ conversation, handleDelete }) {

    const navigate = useNavigate();
    return (
        <Card className="mb-3 card-hover-effect" onClick={() => navigate(`/conversation/${conversation.id}`)}>
            <Card.Body>
                <Card.Title>
                    {conversation.name}
                </Card.Title>
                <Card.Subtitle className="mb-2 text-muted">
                    Participants: {conversation.participants.join(', ')}
                </Card.Subtitle>
                <Card.Text>
                    Messages: {conversation.messages ? conversation.messages.length : 0}
                </Card.Text>
            </Card.Body>
            <Card.Footer>
                <Button variant="primary" className="me-2" onClick={() => navigate(`/conversation/${conversation.id}`)}>View</Button>
                <Button variant="danger" onClick={() => handleDelete(conversation.id)}>Delete</Button>
            </Card.Footer>
        </Card>
    );
}