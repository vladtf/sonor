import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { Container, Row, Col, Card } from 'react-bootstrap';
import axios from 'axios';
import { BACKEND_URL } from '../configuration/BackendConfig';
import { Spinner } from 'react-bootstrap';

function PostDetailPage() {
  const [post, setPost] = useState(null);
  const { postId } = useParams(); // Extract postId from URL

  useEffect(() => {
    axios
      .get(`${BACKEND_URL}/api/posts/${postId}`) // Adjust URL as needed
      .then((response) => {
        setPost(response.data);
      })
      .catch((error) => {
        console.error("Error fetching post details:", error);
      });
  }, [postId]);

  if (!post) {
    return (
      <Container>
        <Row className="justify-content-md-center">
          <Col md={8}>
              <span className="sr-only">Loading...</span>
              <Spinner animation="border" role="status" />
          </Col>
        </Row>
      </Container>
    );
  }

  return (
    <Container>
      <Row className="justify-content-md-center">
        <Col md={8}>
          <Card>
            <Card.Body>
              <Card.Title>{post.title}</Card.Title>
              <Card.Subtitle className="mb-2 text-muted">
                {post.category}
              </Card.Subtitle>
              <Card.Text>{post.content}</Card.Text>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
}

export default PostDetailPage;
