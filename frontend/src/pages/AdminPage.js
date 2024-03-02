import { Col, Container, Row, Table } from "react-bootstrap";
import MyNavbar from "../components/MyNavbar";
import { useEffect, useState } from "react";
import axios from "axios";
import { BACKEND_URL } from "../configuration/BackendConfig";
import { useNavigate } from "react-router-dom";

function AdminPage() {
  const [roles, setRoles] = useState([]);
  const [email, setEmail] = useState("");
  const [role, setRole] = useState("");
  const [emails, setEmails] = useState([]);
  const [posts, setPosts] = useState([]);
  const [updatedBalances, setUpdatedBalances] = useState({});

  const token = localStorage.getItem("jwtToken");
  const navigate = useNavigate();
  useEffect(() => {
    if (!token) {
      delete axios.defaults.headers.common["Authorization"];
      navigate("/login");
    } else {
      axios.defaults.headers.common["Authorization"] = token;
    }
  }, [token, navigate]);


  const getMyRoles = () => {
    axios
      .get(BACKEND_URL + "/roles/my-roles")
      .then((response) => {
        console.log(response.data);
        setRoles(response.data);
      })
      .catch((error) => {
        alert("Error retrieving roles!");
        console.error(error.response.data);
      });
  };

  const addRoleToUser = () => {
    const request = {
      email: email,
      action: role,
    };

    axios
      .post(BACKEND_URL + "/roles/add-role", request)
      .then((response) => {
        console.log(response.data);
        alert("Role added successfully!");
        getMyRoles(); // Fetch roles again after adding a role
      })
      .catch((error) => {
        alert("Error adding role!");
        console.error(error.response.data);
      });
  };

  const fetchPosts = async () => {
    try {
      const response = await axios.get(BACKEND_URL + "/api/posts/all");
      const responseData = response.data;
      setPosts(responseData);
    } catch (error) {
      console.error("Error fetching posts:", error);
    }
  };

  const updateAccountBalance = async (accountId) => {
    try {
      const request = {
        balance: updatedBalances[accountId],
        iban: accountId,
      };
      await axios.put(BACKEND_URL + "/api/accounts", request);
      alert("Account balance updated successfully!");
      fetchPosts(); // Fetch accounts again after updating balance
    } catch (error) {
      console.error("Error updating account balance:", error);
      alert("Error updating account balance!");
    }
  };

  const deleteAccount = async (iban) => {
    try {
      const deleteRequest = {
        iban: iban,
      };

      await axios.delete(`${BACKEND_URL}/api/accounts`, deleteRequest);

      alert("Account deleted successfully!");
      fetchPosts(); // Fetch accounts again after deleting
    } catch (error) {
      console.error("Error deleting account:", error);
      alert("Error deleting account!");
    }
  };

  useEffect(() => {
    const fetchEmails = async () => {
      try {
        const response = await axios.get(BACKEND_URL + "/api/emails");
        const responseData = response.data;
        setEmails(responseData);
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    };

    const fetchMyRoles = async () => {
      try {
        const response = await axios.get(BACKEND_URL + "/roles/my-roles");
        const responseData = response.data;
        setRoles(responseData);
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    };

    fetchEmails();
    fetchMyRoles();
    fetchPosts();
  }, []);

  const handleBalanceChange = (accountId, amount) => {
    setUpdatedBalances((prevBalances) => ({
      ...prevBalances,
      [accountId]: amount,
    }));
  };

  const handleUpdateBalance = (accountId) => {
    const amount = updatedBalances[accountId];
    if (amount !== undefined && amount !== "") {
      updateAccountBalance(accountId);
    }
  };

  return (
    <>
      <Container>
        
        <Row>
          <Col>
            <h3>My Roles</h3>
            {roles.length > 0 ? (
              <Table striped bordered hover>
                <thead>
                  <tr>
                    <th>Roles</th>
                  </tr>
                </thead>
                <tbody>
                  {roles.map((role, index) => (
                    <tr key={index}>
                      <td>{role}</td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            ) : (
              <p>No roles found.</p>
            )}
          </Col>
        </Row>
        <hr />
        <Row>
          <Col>
            <h3>Add Role to User</h3>
            <div className="form-group">
              <label>Email:</label>
              <select
                className="form-select"
                onChange={(e) => setEmail(e.target.value)}
              >
                <option value="">Select an email</option>
                {emails.map((email) => (
                  <option key={email} value={email}>
                    {email}
                  </option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>Role:</label>
              <select
                className="form-select"
                onChange={(e) => setRole(e.target.value)}
              >
                <option value="">Select a role</option>
                <option value="DISPLAY_USERS">DISPLAY_USERS</option>
                <option value="UPDATE_USERS">UPDATE_USERS</option>
                <option value="DISPLAY_ACCOUNTS">DISPLAY_ACCOUNTS</option>
              </select>
            </div>

            <div className="form-group mt-2">
              <button
                className="btn btn-success btn-block"
                onClick={() => addRoleToUser()}
              >
                Add Role
              </button>
            </div>
          </Col>
        </Row>
        <hr />
        <Row>
          <Col>
            <h3>Accounts</h3>
            {posts.length > 0 ? (
              <Table striped bordered hover>
                <thead>
                  <tr>
                    <th>Iban</th>
                    <th>Balance</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {posts.content && posts.content.map((account) => (
                    <tr key={account.iban}>
                      <td>{account.iban}</td>
                      <td>{account.balance}</td>
                      <td>
                        <div className="form-group">
                          <input
                            type="number"
                            className="form-control"
                            placeholder="New Balance"
                            value={updatedBalances[account.iban] || ""}
                            onChange={(e) =>
                              handleBalanceChange(account.iban, e.target.value)
                            }
                          />
                          <button
                            className="btn btn-primary m-2"
                            onClick={() => handleUpdateBalance(account.iban)}
                          >
                            Update Balance
                          </button>
                          <button
                            className="btn btn-danger m-2"
                            onClick={() => deleteAccount(account.iban)}
                          >
                            Delete
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            ) : (
              <p>No accounts found.</p>
            )}
          </Col>
        </Row>
      </Container>
    </>
  );
}

export default AdminPage;
