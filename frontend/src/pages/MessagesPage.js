import { Button, Card, Col, Container, Row } from "react-bootstrap";
import MyNavbar from "../components/MyNavbar";
import { useState, useEffect } from "react";
import axios from "axios";
import { BACKEND_URL } from "../configuration/BackendConfig";
import { useNavigate } from "react-router-dom";
import NewConversation from "../components/NewConversation";
import { FaPlusCircle } from "react-icons/fa";
import ConversationListItem from "../components/ConversationListItem";
import { ToastContainer, toast } from "react-toastify";

function MessagesPage() {
  const [conversations, setConversations] = useState([]);

  const [showNewConversationForm, setShowNewConversationForm] = useState(false);

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
    fetchConversations();
  }, []);

  const fetchConversations = async () => {
    try {
      axios
        .get(BACKEND_URL + "/api/conversations/all")
        .then((response) => {
          console.log(response.data);
          setConversations(response.data);
          if (response.data.length === 0) {
            toast.warning("No conversation found!");
          }
        })
        .catch((error) => {
          toast.error("Error retrieving conversations!");
          console.error(error.response.data);
        });
    } catch (error) {
      console.error(error.response.data);
    }
  };


  return (
    <>
      <ToastContainer />
      <Container>
        <Row>
          <Col>
            <Button variant="success" onClick={() => setShowNewConversationForm(true)}>
              <FaPlusCircle /> New Conversation
            </Button>
            <NewConversation show={showNewConversationForm} setShow={setShowNewConversationForm} fetchConversations={fetchConversations} />
          </Col>
        </Row>
        <hr />
        <Row className="mb-3">
          <Col md={6}>
            <h3>Conversations</h3>
            {conversations.map((conversation, index) => (
              <ConversationListItem key={index} conversation={conversation} fetchConversations={fetchConversations} />
            ))}
          </Col>
        </Row>

        <hr />

      </Container>
    </>
  );
}

export default MessagesPage;
