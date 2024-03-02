import { Alert, Button, Card, Col, Container, Form, ListGroup, Row, ToastContainer } from "react-bootstrap";
import MyNavbar from "../components/MyNavbar";
import { useState, useEffect } from "react";
import axios from "axios";
import { BACKEND_URL } from "../configuration/BackendConfig";
import { useNavigate } from "react-router-dom";
import NewConversation from "../components/NewConversation";
import { FaPlusCircle, FaMinusCircle } from "react-icons/fa";
import ConversationListItem from "../components/ConversationListItem";
import { useParams } from "react-router-dom";
import Message from "../components/Message";
import { toast } from "react-toastify";
import NewParticipant from "../components/NewParticipant";
import { FaUserCircle } from "react-icons/fa";

function ConversationDetailPage() {
  const { conversationId } = useParams();
  const [conversation, setConversation] = useState([]);
  const [message, setMessage] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [showAddParticipantForm, setShowAddParticipantForm] = useState(false);
  const [showRemoveParticipantForm, setShowRemoveParticipantForm] = useState(false);

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

  useEffect(() => {
    fetchConversation();
  }, [conversationId]);

  const fetchConversation = async () => {
    try {
      axios
        .get(BACKEND_URL + "/api/conversations/" + conversationId)
        .then((response) => {
          console.log(response.data);
          setConversation(response.data);

        })
        .catch((error) => {
          toast.error("Error retrieving conversation!");
          console.error(error.response.data);
          navigate("/messages");
        });
    } catch (error) {
      console.error(error.response.data);
    }
  };

  const handleMessageSubmit = async (event) => {
    event.preventDefault();
    try {
      const response = await axios.post(BACKEND_URL + "/api/conversations/addMessage", {
        conversationId: conversationId,
        content: message,
      });
      console.log(response.data);
      setMessage("");
      fetchConversation();
    } catch (error) {
      console.error(error.response.data);
      setError("Error submitting message!");
      toast.error("Error submitting message!");
    }
  }

  const handleDeleteParticipant = async (conversationId, username) => {
    if (!window.confirm("Are you sure you want to remove " + username + " from this conversation?")) {
      return;
    }
    try {
      const response = await axios.post(BACKEND_URL + "/api/conversations/removeUser", {
        conversationId: conversationId,
        username: username,
      });
      console.log(response.data);
      toast.success("Participant removed!");
      fetchConversation();
    }
    catch (error) {
      console.error(error.response.data);
      setError("Error removing participant!");
      toast.error("Error removing participant!");
    }

  }



  return (
    <>
      <ToastContainer />
      <Container className="w-50">
        <Row>
          <Col md={12}>
            <Card>
              <Card.Body>
                <Card.Title>{conversation.name}</Card.Title>
                <Card.Text>
                  {conversation.participants && conversation.participants.length > 0 ? (
                    <ListGroup variant="flush">
                      {conversation.participants.map((participant, index) => (
                        <ListGroup.Item key={index}><FaUserCircle /> {participant}
                          <Button
                            variant="danger"
                            size="sm"
                            onClick={() => handleDeleteParticipant(conversation.id, participant)}
                            style={{ float: 'right' }}
                          >
                            Delete
                          </Button></ListGroup.Item>
                      ))}
                    </ListGroup>
                  ) : (
                    <p>No participants yet.</p>
                  )}
                </Card.Text>
              </Card.Body>
              <Card.Footer>
                <Button variant="success" onClick={() => setShowAddParticipantForm(true)}>
                  <FaPlusCircle /> Add Participant
                </Button>
                <NewParticipant show={showAddParticipantForm} setShow={setShowAddParticipantForm} fetchConversation={fetchConversation} conversationId={conversationId} />
              </Card.Footer>
            </Card>
            <hr />
            <Card>
              <Card.Body>
                <Card.Title>Messsages</Card.Title>
                {conversation.messages ? conversation.messages.map((message, index) => <Message key={index} message={message} fetchConversation={fetchConversation} />) : <p>No messages yet.</p>}
              </Card.Body>
            </Card>
            <hr />
            <Card>
              <Card.Body>
                <Form onSubmit={handleMessageSubmit}>
                  <Form.Group className="mb-3" controlId="message">
                    <Form.Label>New message:</Form.Label>
                    <Form.Control
                      as="textarea"
                      rows={3}
                      value={message}
                      onChange={(e) => setMessage(e.target.value)}
                      disabled={submitting}
                      required
                    />
                  </Form.Group>
                  {error && <Alert variant="danger">{error}</Alert>}
                  <Button variant="primary" type="submit" disabled={submitting}>
                    {submitting ? 'Submitting...' : 'Submit Message'}
                  </Button>
                </Form>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    </>
  );
}

export default ConversationDetailPage;
