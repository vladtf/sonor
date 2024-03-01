import React, { useState } from "react";
import { Container, Row, Col, Form, Button, Spinner } from "react-bootstrap";
import axios from "axios";
import { BACKEND_URL } from "../configuration/BackendConfig";
import { useNavigate } from "react-router-dom";

function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  const handleSubmit = (event) => {
    event.preventDefault();

    const postData = {
      username: username,
      password: password,
    };

    setLoading(true); // Set loading state to true

    console.log("Sending login data: ", postData);
    axios
      .post(BACKEND_URL + "/login", postData)
      .then((response) => {
        console.log(response.data);
        const data = response.data
        const token = data.token;
        const roles = data.roles;
        localStorage.setItem("jwtToken", "Bearer " + token); // Store the token in the local storage
        localStorage.setItem("roles", roles); // Store the roles in the local storage
        axios.defaults.headers.common["Authorization"] = "Bearer " + token; // Set the default Authorization header
        navigate("/home");
      })
      .catch((error) => {
        alert("Login failed!");
        console.error(error.response.data);
      })
      .finally(() => {
        setLoading(false); // Set loading state to false after the request completes
      });
  };

  return (
    <>
      <Container style={{ marginTop: "250px" }}>
        <Row>
          <Col>
            <h2
              className="text-primary"
              style={{
                fontSize: "24px",
                textAlign: "center",
              }}
            >
              Login Page
            </h2>
          </Col>
        </Row>
        <Row>
          <Col md={{ span: 6, offset: 3 }}>
            <Form onSubmit={handleSubmit}>
              <Form.Group controlId="username" className="mb-3">
                <Form.Label>Username:</Form.Label>
                <Form.Control
                  type="text"
                  value={username}
                  onChange={(event) => setUsername(event.target.value)}
                />
              </Form.Group>

              <Form.Group controlId="password" className="mb-3">
                <Form.Label>Password:</Form.Label>
                <Form.Control
                  type="password"
                  value={password}
                  onChange={(event) => setPassword(event.target.value)}
                />
              </Form.Group>

              <div className="d-flex justify-content-center">
                <Button
                  variant="primary"
                  type="submit"
                  style={{
                    width: "200px",
                    height: "50px",
                  }}
                  onClick={handleSubmit}
                  disabled={loading} // Disable the button when loading is true
                >
                  {loading ? ( // Conditionally render the button text or a spinner based on the loading state
                    <Spinner animation="border" size="sm" role="status">
                      <span className="visually-hidden">Loading...</span>
                    </Spinner>
                  ) : (
                    "Login"
                  )}
                </Button>
              </div>
            </Form>

            <Row className="mt-3">
              <Col>
                <p
                  style={{
                    color: "black",
                    fontSize: "15px",
                    textAlign: "center",
                  }}
                >
                  Don't have an account? <a href="/registration">Register</a>
                </p>
              </Col>
            </Row>
          </Col>
        </Row>
      </Container>
    </>
  );
}

export default LoginPage;
