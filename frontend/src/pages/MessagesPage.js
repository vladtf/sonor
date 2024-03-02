import { Col, Container, Row } from "react-bootstrap";
import MyNavbar from "../components/MyNavbar";
import { useState, useEffect } from "react";
import axios from "axios";
import { BACKEND_URL } from "../configuration/BackendConfig";
import { useNavigate } from "react-router-dom";

function MessagesPage() {
  const [conversations, setConversations] = useState([]);
  const [newConversation, setNewConversation] = useState("");

  const [ibanSource, setIbanSource] = useState("");
  const [ibanDest, setIbanDest] = useState("");
  const [amount, setAmount] = useState("");
  const [posts, setPosts] = useState([]);

  const [comments, setComments] = useState([]);
  const [selectedAuthor, setSelectedAuthor] = useState("");

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
        })
        .catch((error) => {
          alert("Error retrieving conversations!");
          console.error(error.response.data);
        });
    } catch (error) {
      console.error(error.response.data);
    }
  };

  const handleNewConversation = (event) => {
    event.preventDefault();
    console.log("Creating new conversation:", newConversation);
    axios
      .post(BACKEND_URL + "/api/conversations/create", {
        name: newConversation,
      })
      .then((response) => {
        console.log(response.data);
        fetchConversations();
      })
      .catch((error) => {
        console.error(error.response.data);
        alert("Error creating conversation!");
      });
  };


  const getShortContent = (content) => {
    return content.length > 10 ? content.substring(0, 10) + "..." : content;
  };

  return (
    <>
      <Container>

        <Row className="mb-3">
          <Col md={6}>
            <h3>Conversations</h3>
            {conversations.map((conversation, index) => (
              <div key={index}>
                <p>{conversation.name}</p>
              </div>
            ))}
            <div className="mb-3">
              <h4>New Conversation</h4>
              <form>
                <div className="mb-3">
                  <label htmlFor="newConversation" className="form-label">Conversation Name</label>
                  <input
                    type="text"
                    className="form-control"
                    id="newConversation"
                    placeholder="Enter conversation name"
                    value={newConversation}
                    onChange={(event) => setNewConversation(event.target.value)} />
                </div>
                <button type="submit" className="btn btn-primary" onClick={handleNewConversation}>
                  Create
                </button>
              </form>
            </div>
          </Col>
        </Row>

        <hr />

        <Row className="mb-3">
          <Col md={6}>
            <h3>My Comments</h3>
            <div className="form-group">
              <label htmlFor="author">Author:</label>
              <select
                className="form-select"
                id="ibanSource"
                /* onChange={(e) => updateComment(e)} */
                value={selectedAuthor}
                onChange={(e) => setSelectedAuthor(e.target.value)}
              >
                <option value="">All Authors</option>
                {posts.map((account) => (
                  <option key={account.id} value={account.iban}>
                    {account.iban}
                  </option>
                ))}
              </select>
            </div>
          </Col>
        </Row>

        <Row>
          <Col>
            <table className="table table-striped">
              <thead>
                <tr>
                  <th scope="col">Author</th>
                  <th scope="col">Post</th>
                  <th scope="col">Content</th>
                  <th scope="col">Date</th>
                </tr>
              </thead>
              <tbody>
                {comments.map((comment) => (
                  <tr
                    key={comment.id}
                  >
                    <td>{comment.author}</td>
                    <td><a href={`/post/${comment.postId}`}>Post {comment.postId}</a></td>
                    <td>{comment.content}</td>
                    <td>{comment.createdAt}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </Col>
        </Row>
      </Container>
    </>
  );
}

export default MessagesPage;
