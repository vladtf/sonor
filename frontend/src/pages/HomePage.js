import { Container, Row, Col, Card, Button, ListGroup } from "react-bootstrap";
import MyNavbar from "../components/MyNavbar";
import { useEffect, useState } from "react";
import axios from "axios";
import { BACKEND_URL } from "../configuration/BackendConfig";
import { useNavigate } from "react-router-dom";
import { ToastContainer, toast } from "react-toastify";
import { FaRegNewspaper, FaRegClock, FaRegUser, FaRegCommentDots, FaRegComments } from 'react-icons/fa';


function HomePage() {
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

  const [posts, setPosts] = useState([]);
  const [messages, setMessages] = useState([]);
  const [exchangeResults, setExchangeResults] = useState([]);
  const [news, setNews] = useState([]);
  const [conversations, setConversations] = useState([]);

  useEffect(() => {
    fetchPosts();
    fetchMessages();
    fetchExchangeResults();
    fetchNews();
    fetchConversations();
  }, []);

  const fetchPosts = () => {
    axios
      .get(BACKEND_URL + "/api/posts/all")
      .then((response) => {
        console.log(response.data);
        setPosts(response.data);
      })
      .catch((error) => {
        toast.error("Error retrieving posts!");
        console.error(error.response.data);
      });
  };

  const fetchMessages = () => {
    axios
      .get(BACKEND_URL + "/api/messages/mine")
      .then((response) => {
        console.log(response.data);
        setMessages(response.data);
      })
      .catch((error) => {
        toast.error("Error retrieving messages!");
        console.error(error.response.data);
      });
  };

  const fetchExchangeResults = () => {

    axios
      .get(BACKEND_URL + "/api/exchange")
      .then((response) => {
        console.log(response.data);
        setExchangeResults(response.data);
      })
      .catch((error) => {
        toast.error("Error retrieving exchange results!");
        console.error(error.response.data);
      });
  };

  const fetchNews = () => {
    axios
      .get(BACKEND_URL + "/api/news")
      .then((response) => {
        console.log(response.data);
        setNews(response.data);
      })
      .catch((error) => {
        toast.error("Error retrieving news!");
        console.error(error.response.data);
      });
  };

  const fetchConversations = () => {
    axios
      .get(BACKEND_URL + "/api/conversations/all")
      .then((response) => {
        console.log(response.data);
        setConversations(response.data);
      })
      .catch((error) => {
        toast.error("Error retrieving conversations!");
        console.error(error.response.data);
      });
  }

  const getShortenedContent = (content) => {
    if (content.length > 10) {
      return content.substring(0, 10) + "...";
    } else {
      return content;
    }
  };

  return (
    <>
      <ToastContainer />
      <Container>
        <Row className="mt-4">
          <Col>
            <Card>
              <Card.Header>Recent Messages</Card.Header>
              <Card.Body>
                {messages.length === 0 ? (
                  <p>No recent messages found.</p>
                ) : (
                  messages.slice(0, 5).map((message, index) => (
                    <Card
                      key={index}
                      className="card-hover-effect mb-3"
                      onClick={() => navigate(`/conversation/${message.conversationId}`)}
                    >
                      <Card.Body>
                        <Card.Title><FaRegUser className="me-2" />{message.author}</Card.Title>
                        <Card.Text><FaRegCommentDots className="me-2" />{getShortenedContent(message.content)}</Card.Text>
                        <Card.Text className="text-muted"><FaRegClock className="me-2" />{new Date(message.createdAt).toLocaleDateString()}</Card.Text>
                      </Card.Body>
                      <Card.Footer className="text-muted">
                        <small>Conversation: {message.conversationName}</small>
                      </Card.Footer>
                    </Card>
                  ))
                )}
              </Card.Body>
              <Card.Footer>
                <Button variant="primary" href="/messages">
                  View All Messages
                </Button>
              </Card.Footer>
            </Card>

            <Card className="mt-4">
              <Card.Header>All Conversations</Card.Header>
              <Card.Body>
                {conversations.length === 0 ? (
                  <p>No conversations found.</p>
                ) : (
                  conversations.map((conversation, index) => (
                    <Card
                      key={index}
                      className="card-hover-effect mb-3"
                      onClick={() => navigate(`/conversation/${conversation.id}`)}
                    >
                      <Card.Body>
                        <Card.Title className="mb-0"><FaRegComments className="me-2" />{conversation.name}</Card.Title>
                      </Card.Body>
                      <Card.Footer className="text-muted">
                        <small><FaRegUser className="me-1" />Participants: {conversation.participants.join(', ')}</small>
                      </Card.Footer>
                    </Card>
                  ))
                )}
              </Card.Body>
              <Card.Footer>
                <Button variant="primary" href="/conversations">
                  View All Conversations
                </Button>
              </Card.Footer>
            </Card>

            <Card className="mt-4">
              <Card.Header>Exchange Values</Card.Header>
              <Card.Body>
                {exchangeResults.length === 0 ? (
                  <p>No exchange results found.</p>
                ) : (
                  <table className="table table-striped">
                    <thead>
                      <tr>
                        <th>From</th>
                        <th>To</th>
                        <th>Amount</th>
                        <th>Result</th>
                      </tr>
                    </thead>
                    <tbody>
                      {exchangeResults.map((result, index) => (
                        <tr key={index}>
                          <td>{result.from}</td>
                          <td>{result.to}</td>
                          <td>{result.amount}</td>
                          <td>{result.result}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                )}
              </Card.Body>
            </Card>
          </Col>
          <Col>
            <Card>
              <Card.Header>Posts</Card.Header>
              <Card.Body>
                {posts.length === 0 ? (
                  <p>No posts found.</p>
                ) : (
                  posts.map((post, index) => (
                    <Card key={index} className="mb-3 card-hover-effect" onClick={() => navigate(`/post/${post.id}`)}>
                      <Card.Body>
                        <Card.Title><FaRegNewspaper className="me-2" />{post.title}</Card.Title>
                        <Card.Subtitle className="mb-2 text-muted">
                          {post.category}
                        </Card.Subtitle>
                        <Card.Text>{post.content}</Card.Text>
                      </Card.Body>
                      <Card.Footer className="text-muted">
                        <small><FaRegClock className="me-1" />{new Date(post.createdAt).toLocaleDateString()}</small>
                        <small className="ms-3"><FaRegUser className="me-1" />{post.author}</small>
                      </Card.Footer>
                    </Card>
                  ))
                )}
              </Card.Body>
              <Card.Footer>
                <Button variant="primary" href="/posts">
                  View All Posts
                </Button>
              </Card.Footer>
            </Card>

            <Card className="mt-4">
              <Card.Header>News</Card.Header>
              <Card.Body>
                {news.length === 0 ? (
                  <p>No news found.</p>
                ) : (
                  <ListGroup variant="flush">
                    {news.map((item, index) => (
                      <ListGroup.Item key={index}>
                        <h5 className="card-title">{item.title}</h5>
                        <p className="card-text">{item.description}</p>
                      </ListGroup.Item>
                    ))}
                  </ListGroup>
                )}
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    </>
  );
}

export default HomePage;
