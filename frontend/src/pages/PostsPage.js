import { Col, Container, Row, Button, Card } from "react-bootstrap";
import MyNavbar from "../components/MyNavbar";
import axios from "axios";
import { useState, useEffect } from "react";
import BackendConfig, { BACKEND_URL } from "../configuration/BackendConfig";
import { useNavigate } from "react-router-dom";

function PostPage() {
  const [posts, setPosts] = useState([]);
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [category, setCategory] = useState("OTHER");

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
    getPosts();
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

  const addPost = (e) => {
    // check if the title and content are empty
    if (title === "" || content === "") {
      alert("Title and content cannot be empty!");
      return;
    }

    const confirmAdd = window.confirm(
      "Are you sure you want to add this post?"
    );
    if (!confirmAdd) {
      return;
    }


    const newPostRequest = {
      title: title,
      category: category,
      content: content,
    };

    console.log("Sending post data: ", newPostRequest);


    axios
      .post(BACKEND_URL + "/api/posts/create", newPostRequest)
      .then((response) => {
        console.log(response.data);
        setPosts(response.data);
      })
      .catch((error) => {
        alert("Error adding post!");
        console.error(error.response.data);
      });
  };

  const deletePost = (id) => {
    const confirmDelete = window.confirm(
      "Are you sure you want to delete this post?"
    );

    if (!confirmDelete) {
      return;
    }

    const deleteRequest = {
      id: id,
    };

    axios
      .delete(BACKEND_URL + "/api/posts/delete", { data: deleteRequest })
      .then((response) => {
        console.log(response.data);
        alert("Post deleted successfully!");
        getPosts();
      })
      .catch((error) => {
        alert("Error deleting post!");
        console.error(error.response.data);
      });
  };

  const getShortContent = (content) => {
    if (content.length > 15) {
      return content.substring(0, 15) + "...";
    }
    return content;
  }

  return (
    <>
      <Container>
        
        <Row className="mb-3">
          <Col md={6}>
            <form onSubmit={(e) => e.preventDefault()}>
              <div className="form-group mb-3">
                <label htmlFor="title">Title:</label>
                <input
                  type="text"
                  className="form-control"
                  id="title"
                  placeholder="Enter title"
                  value={title}
                  onChange={(event) => setTitle(event.target.value)}
                />
              </div>
              <div className="form-group mb-3">
                <label htmlFor="content">Content:</label>
                <textarea
                  className="form-control"
                  id="content"
                  rows="3"
                  value={content}
                  onChange={(event) => setContent(event.target.value)}
                ></textarea>
              </div>
              <div className="form-group mb-3">
                <label htmlFor="category">Category:</label>
                <select
                  className="form-select"
                  id="category"
                  value={category}
                  onChange={(event) => setCategory(event.target.value)}
                >
                  <option value="OTHER">Other</option>
                  <option value="TECHNOLOGY">Technology</option>
                  <option value="SPORTS">Sports</option>
                </select>
              </div>
            </form>
          </Col>
        </Row>
        <Row>
          <Col>
            <Button variant="success" onClick={addPost}>
              Add Post
            </Button>
          </Col>
        </Row>
        <hr />
        <Row>
          {posts.length === 0 ? (
            <h2 className="text-center">No posts found</h2>
          ) : (
            posts.map((post, index) => {
              return (
                <Col md="3" key={index} className="p-2">
                  <Card  className="card-hover-effect" onClick={() => navigate(`/post/${post.id}`)}>
                    <Card.Body>
                      <Card.Title>{post.title}</Card.Title>
                      <Card.Subtitle className="mb-2 text-muted">
                        {post.category}
                      </Card.Subtitle>
                      <Card.Text>{getShortContent(post.content)}</Card.Text>
                      <Button
                        variant="danger"
                        onClick={() => deletePost(post.id)}
                      >
                        Delete Post
                      </Button>
                    </Card.Body>
                  </Card>
                </Col>
              );
            })
          )}
        </Row>
      </Container>
    </>
  );
}

export default PostPage;
