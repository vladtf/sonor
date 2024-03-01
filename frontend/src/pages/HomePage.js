import { Container, Row, Col, Card, Button, ListGroup } from "react-bootstrap";
import MyNavbar from "../components/MyNavbar";
import { useEffect, useState } from "react";
import axios from "axios";
import { BACKEND_URL } from "../configuration/BackendConfig";
import { useNavigate } from "react-router-dom";


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
  const [transactions, setTransactions] = useState([]);
  const [exchangeResults, setExchangeResults] = useState([]);
  const [news, setNews] = useState([]);


  useEffect(() => {
    getPosts();
    // getTransactions();
    getExchangeResults();
    getNews();
  }, []);

  const getPosts = () => {
    axios
      .get(BACKEND_URL + "/api/posts/all")
      .then((response) => {
        console.log(response.data);
        setPosts(response.data);
      })
      .catch((error) => {
        alert("Error retrieving posts!");
        console.error(error.response.data);
      });
  };

  const getTransactions = () => {
    axios
      .get(BACKEND_URL + "/api/transactions")
      .then((response) => {
        console.log(response.data);
        setTransactions(response.data);
      })
      .catch((error) => {
        alert("Error retrieving transactions!");
        console.error(error.response.data);
      });
  };

  const getExchangeResults = () => {

    axios
      .get(BACKEND_URL + "/api/exchange")
      .then((response) => {
        console.log(response.data);
        setExchangeResults(response.data);
      })
      .catch((error) => {
        alert("Error retrieving exchange results!");
        console.error(error.response.data);
      });
  };

  const getNews = () => {
    axios
      .get(BACKEND_URL + "/api/news")
      .then((response) => {
        console.log(response.data);
        setNews(response.data);
      })
      .catch((error) => {
        alert("Error retrieving news!");
        console.error(error.response.data);
      });
  };

  return (
    <>
      <Container>
        <MyNavbar />
        <Row className="mt-4">
          <Col>
            <Card>
              <Card.Header>Recent Transactions</Card.Header>
              <Card.Body>
                {transactions.length === 0 ? (
                  <p>No recent transactions found.</p>
                ) : (
                  transactions.slice(0, 5).map((transaction, index) => (
                    <Card
                      key={index}
                      className={
                        transaction.transactionType === "INCOME"
                          ? "bg-success text-white mb-3"
                          : "bg-danger text-white mb-3"
                      }
                      style={{ height: "5rem" }}
                    >
                      <Card.Body>
                        <Card.Title>Amount: {transaction.sum}</Card.Title>
                        <Card.Text>Date: {transaction.createdAt}</Card.Text>
                      </Card.Body>
                    </Card>
                  ))
                )}
              </Card.Body>
              <Card.Footer>
                <Button variant="primary" href="/transactions">
                  View All Transactions
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
                    <Card key={index} className="mb-3">
                      <Card.Body>
                        <Card.Title>{post.title}</Card.Title>
                        <Card.Subtitle className="mb-2 text-muted">
                          {post.category}
                        </Card.Subtitle>
                        <Card.Text>{post.content}</Card.Text>
                      </Card.Body>
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
