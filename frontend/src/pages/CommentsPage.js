import { Col, Container, Row } from "react-bootstrap";
import MyNavbar from "../components/MyNavbar";
import { useState, useEffect } from "react";
import axios from "axios";
import { BACKEND_URL } from "../configuration/BackendConfig";
import { useNavigate } from "react-router-dom";

function CommentsPage() {
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
    fetchPosts();
    fetchComments();
  }, []);

  const fetchPosts = () => {

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

  const fetchComments = async () => {
    try {
      const response = await axios.get(BACKEND_URL + "/api/comments/all");

      console.log(response.data);
      setComments(response.data);
    } catch (error) {
      console.error(error.response.data);
    }
  };

  const updateComment = async (event) => {
    const iban = event.target.value;
    setSelectedAuthor(iban);

    try {
      const response = await axios.get(
        BACKEND_URL + `/api/transactions${iban ? `?iban=${iban}` : ""}`
      );
      console.log(response.data);
      setComments(response.data);
    } catch (error) {
      console.error(error.response.data);
    }
  };

  const addComment = () => {
    const transactionRequest = {
      ibanSource: ibanSource,
      ibanDest: ibanDest,
      sum: amount,
    };

    const confirmTransaction = window.confirm(
      `Are you sure you want to send ${amount} from ${ibanSource} to ${ibanDest}?`
    );

    if (!confirmTransaction) {
      return;
    }

    console.log("Add comment: ", transactionRequest);

    axios
      .post(BACKEND_URL + "/api/transaction", transactionRequest)
      .then((response) => {
        console.log(response.data);
        alert("Comment successful!");
        fetchComments(); // Fetch transactions again after successful transaction
      })
      .catch((error) => {
        alert("Error sending transaction!");
        console.error(error.response.data);
      });
  };

  const getShortContent = (content) => {
    return content.length > 10 ? content.substring(0, 10) + "..." : content;
  };

  return (
    <>
      <Container>
        
        <Row>
          <Col md={6}>
            <div className="mb-3">
              <label htmlFor="ibanSource" className="form-label">
                My account IBAN
              </label>
              <select
                className="form-select"
                id="ibanSource"
                onChange={(e) => setIbanSource(e.target.value)}
                value={ibanSource}
              >
                <option value="">Select an account</option>
                {posts.map((account) => (
                  <option key={account.iban} value={account.iban}>
                    {account.iban}
                  </option>
                ))}
              </select>
            </div>
            <div className="mb-3">
              <label htmlFor="ibanDest" className="form-label">
                Destination account IBAN
              </label>
              <select
                className="form-select"
                id="ibanDest"
                onChange={(e) => setIbanDest(e.target.value)}
                value={ibanDest}
              >
                <option value="">Select an account</option>
                {posts.map((account) => (
                  <option key={account.iban} value={account.iban}>
                    {account.iban}
                  </option>
                ))}
              </select>
            </div>
            <div className="mb-3">
              <label htmlFor="amount" className="form-label">
                Amount
              </label>
              <input
                type="number"
                className="form-control"
                id="amount"
                placeholder="Enter amount"
                value={amount}
                onChange={(event) => setAmount(event.target.value)}
              />
            </div>
            <div className="mb-3">
              <button className="btn btn-primary" onClick={addComment}>
                Send Transaction
              </button>
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
                onChange={(e) => updateComment(e)}
                value={selectedAuthor}
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

export default CommentsPage;
