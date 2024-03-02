import { Button, Card, Col, Container, Pagination, Row } from "react-bootstrap";
import MyNavbar from "../components/MyNavbar";
import { useState, useEffect } from "react";
import axios from "axios";
import { BACKEND_URL } from "../configuration/BackendConfig";
import { useNavigate } from "react-router-dom";
import NewConversation from "../components/NewConversation";
import { FaPlusCircle } from "react-icons/fa";
import ConversationListItem from "../components/ConversationListItem";
import { ToastContainer, toast } from "react-toastify";

function ConversationsPage() {
  const [conversations, setConversations] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);


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

  const fetchConversations = (pageNumber = 0, pageSize = 3) => {
    axios
      .get(BACKEND_URL + "/api/conversations/all", {
        params: {
          page: pageNumber,
          size: pageSize,
        },
      })
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
  };
  const handlePageChange = (pageNumber) => {
    if (pageNumber < 0 || pageNumber > conversations.totalPages + 1) {
      return;
    }

    setCurrentPage(pageNumber);
    fetchConversations(pageNumber - 1);
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
        <Row>
          <Col>
            <Pagination className="mb-0">
              <Pagination.Prev onClick={() => handlePageChange(currentPage - 1)} disabled={currentPage === 1} />
              {[...Array(conversations.totalPages).keys()].map(pageNumber => (
                <Pagination.Item key={pageNumber + 1} active={pageNumber + 1 === currentPage} onClick={() => handlePageChange(pageNumber + 1)}>
                  {pageNumber + 1}
                </Pagination.Item>
              ))}
              <Pagination.Next onClick={() => handlePageChange(currentPage + 1)} disabled={currentPage === conversations.totalPages} />
            </Pagination>
          </Col>
        </Row>
        <hr />
        <Row className="mt-3">
          <Col>
            {conversations.content && conversations.content.map((conversation, index) => (
              <ConversationListItem key={index} conversation={conversation} fetchConversations={fetchConversations} />
            ))}
          </Col>
        </Row>
      </Container>
    </>
  );
}

export default ConversationsPage;
