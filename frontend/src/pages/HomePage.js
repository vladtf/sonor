import { Container, Row, Col, Card, Button, ListGroup, Pagination } from "react-bootstrap";
import MyNavbar from "../components/MyNavbar";
import { useEffect, useState } from "react";
import axios from "axios";
import { BACKEND_URL } from "../configuration/BackendConfig";
import { useNavigate } from "react-router-dom";
import { ToastContainer, toast } from "react-toastify";
import { FaRegNewspaper, FaRegClock, FaRegUser, FaRegCommentDots, FaRegComments } from 'react-icons/fa';
import { WiDaySunny, WiDayCloudy, WiRain, WiSnow } from 'react-icons/wi';
import ShowErrorToast from "../exception/ToastUtils";

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

  const [posts, setPosts] = useState([{ content: [] }]);
  const [messages, setMessages] = useState([]);
  const [weatherForecast, setWeatherForecast] = useState([]);
  const [conversations, setConversations] = useState([]);

  const [currentPagePosts, setCurrentPagePosts] = useState(1);
  const [currentPageMessages, setCurrentPageMessages] = useState(1);
  const [currentPageConversations, setCurrentPageConversations] = useState(1);

  useEffect(() => {
    fetchPosts();
    fetchMessages();
    fetchWeatherForecast();
    fetchConversations();
  }, []);

  const fetchPosts = (pageNumber = 0, pageSize = 2) => {
    axios
      .get(BACKEND_URL + "/api/posts/all", {
        params: {
          page: pageNumber,
          size: pageSize
        }
      })
      .then((response) => {
        console.log(response.data);
        setPosts(response.data);
      })
      .catch((error) => {
        ShowErrorToast(error, "Error retrieving posts!");
      });
  };

  const fetchMessages = (pageNumber = 0, pageSize = 2) => {
    axios
      .get(BACKEND_URL + "/api/messages/mine", {
        params: {
          page: pageNumber,
          size: pageSize
        }
      })
      .then((response) => {
        console.log(response.data);
        setMessages(response.data);
      })
      .catch((error) => {
        ShowErrorToast(error, "Error retrieving messages!");
      });
  };


  const fetchConversations = (pageNumber = 0, pageSize = 2) => {
    axios
      .get(BACKEND_URL + "/api/conversations/all", {
        params: {
          page: pageNumber,
          size: pageSize
        }
      })
      .then((response) => {
        console.log(response.data);
        setConversations(response.data);
      })
      .catch((error) => {
        ShowErrorToast(error, "Error retrieving conversations!");
      });
  }

  const fetchWeatherForecast = () => {

    axios
      .get(BACKEND_URL + "/api/weather/all")
      .then((response) => {
        console.log(response.data);
        setWeatherForecast(response.data);
      })
      .catch((error) => {
        ShowErrorToast(error, "Error retrieving weather forecast!");
      });
  };

  const handlePageChangePosts = (pageNumber) => {
    if (pageNumber < 0 || pageNumber > posts.totalPages + 1) {
      return;
    }

    setCurrentPagePosts(pageNumber);
    fetchPosts(pageNumber - 1);
  };

  const handlePageChangeMessages = (pageNumber) => {
    if (pageNumber < 0 || pageNumber > messages.totalPages + 1) {
      return;
    }

    setCurrentPageMessages(pageNumber);
    fetchMessages(pageNumber - 1);
  };

  const handlePageChangeConversations = (pageNumber) => {
    if (pageNumber < 0 || pageNumber > conversations.totalPages + 1) {
      return;
    }

    setCurrentPageConversations(pageNumber);
    fetchConversations(pageNumber - 1);
  };

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
                {messages.content && messages.content.length === 0 ? (
                  <p>No recent messages found.</p>
                ) : (
                  messages.content && messages.content.map((message, index) => (
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
                <Container>
                  <Row>
                    <Col md={6} className="d-flex align-items-center">
                      <Button variant="primary" href="/conversations">
                        Go to Messages
                      </Button>
                    </Col>
                    <Col md={6} className="d-flex justify-content-end">
                      <Pagination className="mb-0">
                        <Pagination.Prev onClick={() => handlePageChangeMessages(currentPageMessages - 1)} disabled={currentPageMessages === 1} />
                        {[...Array(messages.totalPages).keys()].map(pageNumber => (
                          <Pagination.Item key={pageNumber + 1} active={pageNumber + 1 === currentPageMessages} onClick={() => handlePageChangeMessages(pageNumber + 1)}>
                            {pageNumber + 1}
                          </Pagination.Item>
                        ))}
                        <Pagination.Next onClick={() => handlePageChangeMessages(currentPageMessages + 1)} disabled={currentPageMessages === messages.totalPages} />
                      </Pagination>
                    </Col>
                  </Row>
                </Container>
              </Card.Footer>
            </Card>

            <Card className="mt-4">
              <Card.Header>All Conversations</Card.Header>
              <Card.Body>
                {conversations.content && conversations.content.length === 0 ? (
                  <p>No conversations found.</p>
                ) : (
                  conversations.content && conversations.content.map((conversation, index) => (
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
                <Container>
                  <Row>
                    <Col md={6} className="d-flex align-items-center">
                      <Button variant="primary" href="/conversations">
                        Go to Conversations
                      </Button>
                    </Col>
                    <Col md={6} className="d-flex justify-content-end">
                      <Pagination className="mb-0">
                        <Pagination.Prev onClick={() => handlePageChangeConversations(currentPageConversations - 1)} disabled={currentPageConversations === 1} />
                        {[...Array(conversations.totalPages).keys()].map(pageNumber => (
                          <Pagination.Item key={pageNumber + 1} active={pageNumber + 1 === currentPageConversations} onClick={() => handlePageChangeConversations(pageNumber + 1)}>
                            {pageNumber + 1}
                          </Pagination.Item>
                        ))}
                        <Pagination.Next onClick={() => handlePageChangeConversations(currentPageConversations + 1)} disabled={currentPageConversations === conversations.totalPages} />
                      </Pagination>
                    </Col>
                  </Row>
                </Container>
              </Card.Footer>
            </Card>


          </Col>
          <Col>
            <Card>
              <Card.Header>Posts</Card.Header>
              <Card.Body>
                {posts.content && posts.content.length === 0 ? (
                  <p>No posts found.</p>
                ) : (
                  posts.content && posts.content.map((post, index) => (
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
                <Container>
                  <Row>
                    <Col md={6} className="d-flex align-items-center">
                      <Button variant="primary" href="/posts">
                        Go to Posts
                      </Button>
                    </Col>
                    <Col md={6} className="d-flex justify-content-end">
                      <Pagination className="mb-0">
                        <Pagination.Prev onClick={() => handlePageChangePosts(currentPagePosts - 1)} disabled={currentPagePosts === 1} />
                        {[...Array(posts.totalPages).keys()].map(pageNumber => (
                          <Pagination.Item key={pageNumber + 1} active={pageNumber + 1 === currentPagePosts} onClick={() => handlePageChangePosts(pageNumber + 1)}>
                            {pageNumber + 1}
                          </Pagination.Item>
                        ))}
                        <Pagination.Next onClick={() => handlePageChangePosts(currentPagePosts + 1)} disabled={currentPagePosts === posts.totalPages} />
                      </Pagination>
                    </Col>
                  </Row>
                </Container>

              </Card.Footer>
            </Card>

            <Card className="mt-4">
              <Card.Header>Weather Forecast</Card.Header>
              <Card.Body>
                {weatherForecast.length === 0 ? (
                  <p>No weather forecast data found.</p>
                ) : (
                  <table className="table table-striped table-hover table-responsive-md">
                    <thead >
                      <tr>
                        <th>Date</th>
                        <th>Temperature</th>
                        <th>Humidity</th>
                        <th>Condition</th>
                      </tr>
                    </thead>
                    <tbody>
                      {weatherForecast.map((forecast, index) => (
                        <tr key={index}>
                          <td>{forecast.date}</td>
                          <td>{forecast.temperature}°C</td>
                          <td>{forecast.humidity}%</td>
                          <td>
                            {forecast.condition === 'Sunny' && <WiDaySunny />}
                            {forecast.condition === 'Cloudy' && <WiDayCloudy />}
                            {forecast.condition === 'Rainy' && <WiRain />}
                            {forecast.condition === 'Snowy' && <WiSnow />}
                            {forecast.condition}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                )}
              </Card.Body>
            </Card>
          </Col>
        </Row>
        <hr />
      </Container>
    </>
  );
}

export default HomePage;
