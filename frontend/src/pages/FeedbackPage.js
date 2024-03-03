import { Col, Container, Row, Button, Card, Form, Pagination } from "react-bootstrap";
import MyNavbar from "../components/MyNavbar";
import axios from "axios";
import { useState, useEffect } from "react";
import BackendConfig, { BACKEND_URL } from "../configuration/BackendConfig";
import { useNavigate } from "react-router-dom";
import NewPost from "../components/NewPost";
import { FaPlusCircle } from "react-icons/fa";
import { ToastContainer, toast } from "react-toastify";
import Feedback from "../components/Feedback";
import ShowErrorToast from "../exception/ToastUtils";

function FeedbackPage() {
  const [feedbacks, setFeedbacks] = useState([]);
  const [consent, setConsent] = useState(false);
  const [satisfaction, setSatisfaction] = useState("neutral")
  const [content, setContent] = useState("");
  const [selectedFeature, setSelectedFeature] = useState("All");
  const [currentPage, setCurrentPage] = useState(0);


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
    fetchFeedbacks();
  }, []);

  const fetchFeedbacks = (pageNumber = 0, pageSize = 2) => {
    axios
      .get(BACKEND_URL + "/api/feedbacks/all", {
        params: {
          page: pageNumber,
          size: pageSize,
        },
      })
      .then((response) => {
        console.log(response.data);
        setFeedbacks(response.data);
      })
      .catch((error) => {
        ShowErrorToast(error, "Error retrieving feedbacks!");
      });
  }

  const handleNewFeedback = () => {
    if (!consent) {
      toast.error("You must agree to the terms and conditions");
      return;
    }

    axios
      .post(BACKEND_URL + "/api/feedbacks/create", {
        content: content,
        satisfaction: satisfaction,
        feature: selectedFeature
      })
      .then((response) => {
        console.log(response.data);
        toast.success("Feedback submitted successfully!");
        fetchFeedbacks();
      }
      )
      .catch((error) => {
        ShowErrorToast(error, "Error submitting feedback!");
      });
  }


  const handlePageChange = (pageNumber) => {
    if (pageNumber < 0 || pageNumber > feedbacks.totalPages + 1) {
      return;
    }

    setCurrentPage(pageNumber);
    fetchFeedbacks(pageNumber - 1);
  };

  return (
    <>
      <ToastContainer />
      <Container className="w-50">
        <Row>
          <Col>
            <Form onAbort={(e) => e.preventDefault()}>
              <Form.Group controlId="content" >
                <Form.Label>Feedback</Form.Label>
                <Form.Control
                  as="textarea"
                  rows={3}
                  placeholder="Enter feedback"
                  value={content}
                  onChange={(e) => setContent(e.target.value)}
                />
              </Form.Group>

              <Form.Group controlId="select" className="mt-3">
                <Form.Label>Which backend feature is this feedback about?</Form.Label>
                <Form.Select onChange={(e) => setSelectedFeature(e.target.value)}>
                  <option value="All">All</option>
                  <option value="Registration">Registration</option>
                  <option value="Posts">Posts</option>
                  <option value="Comments">Comments</option>
                  <option value="Messages">Messages</option>
                  <option value="Feedbacks">Feedbacks</option>
                  <option value="Login">Login</option>
                </Form.Select>
              </Form.Group>

              <Form.Group controlId="radio" className="mt-3">
                <Form.Label>How satisfied are you?</Form.Label>
                <Form.Check
                  type="radio"
                  label="Very satisfied"
                  name="radioOptions"
                  id="radio-1"
                  onChange={() => setSatisfaction("very satisfied")}
                />
                <Form.Check
                  type="radio"
                  label="Satisfied"
                  name="radioOptions"
                  id="radio-2"
                  onChange={() => setSatisfaction("satisfied")}
                />
                <Form.Check
                  type="radio"
                  label="Neutral"
                  name="radioOptions"
                  id="radio-3"
                  onChange={() => setSatisfaction("neutral")}
                />
                <Form.Check
                  type="radio"
                  label="Unsatisfied"
                  name="radioOptions"
                  id="radio-4"
                  onChange={() => setSatisfaction("unsatisfied")}
                />
              </Form.Group>
              <Form.Group controlId="checkbox" className="mt-3">
                <Form.Check
                  type="checkbox"
                  label="I agree to the terms and conditions"
                  value={consent} onChange={(e) => setConsent(e.target.checked)} />
              </Form.Group>

              <Button variant="primary" type="submit" className="mt-3" onClick={handleNewFeedback}>
                Submit
              </Button>
            </Form>
          </Col>
        </Row>
        <hr />
        <Row>
          <Col>
            <Pagination className="mb-0">
              <Pagination.Prev onClick={() => handlePageChange(currentPage - 1)} disabled={currentPage === 1} />
              {[...Array(feedbacks.totalPages).keys()].map(pageNumber => (
                <Pagination.Item key={pageNumber + 1} active={pageNumber + 1 === currentPage} onClick={() => handlePageChange(pageNumber + 1)}>
                  {pageNumber + 1}
                </Pagination.Item>
              ))}
              <Pagination.Next onClick={() => handlePageChange(currentPage + 1)} disabled={currentPage === feedbacks.totalPages} />
            </Pagination>
          </Col>
        </Row>
        <hr />
        <Row>
          <Col>
            {feedbacks.content && feedbacks.content.length === 0 ? (
              <h2 className="text-center">No feedbacks found</h2>
            ) : (
              feedbacks.content && feedbacks.content.map((feedback, index) => <Feedback key={index} feedback={feedback} fetchFeedbacks={fetchFeedbacks} />)
            )}
          </Col>
        </Row>
        <hr />
      </Container>
    </>
  );
}

export default FeedbackPage;
