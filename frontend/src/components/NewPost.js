import axios from "axios";
import { useState } from "react";
import { BACKEND_URL } from "../configuration/BackendConfig";
import { Button, Form, Modal } from "react-bootstrap";

// modal form for new post
export default function NewPost({ fetchPosts, show, setShow }) {
    const [title, setTitle] = useState("");
    const [category, setCategory] = useState("");
    const [content, setContent] = useState("");


    const handleNewPost = (e) => {
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
                fetchPosts();
            })
            .catch((error) => {
                alert("Error adding post!");
                console.error(error.response.data);
            });

        // clear the form
        setTitle("");
        setCategory("");
        setContent("");
        setShow(false);
    };

    return (
        // <Modal show={show} onHide={() => setShow(false)}>
        <Modal show={show} onHide={() => setShow(false)}>
            <Modal.Header closeButton>
                <Modal.Title>New Post</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form>
                    <Form.Group controlId="title">
                        <Form.Label>Title</Form.Label>
                        <Form.Control
                            type="text"
                            placeholder="Enter title"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                        />
                    </Form.Group>
                    <Form.Group controlId="category">
                        <Form.Label>Category</Form.Label>
                        <Form.Control
                            as="select"
                            value={category}
                            onChange={(e) => setCategory(e.target.value)}
                        >
                            <option value="OTHER">Other</option>
                            <option value="TECHNOLOGY">Technology</option>
                            <option value="SPORTS">Sports</option>
                        </Form.Control>
                    </Form.Group>
                    <Form.Group controlId="content">
                        <Form.Label>Content</Form.Label>
                        <Form.Control
                            as="textarea"
                            rows={3}
                            value={content}
                            onChange={(e) => setContent(e.target.value)}
                        />
                    </Form.Group>
                </Form>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={() => setShow(false)}>
                    Close
                </Button>
                <Button variant="primary" onClick={() => handleNewPost()}>
                    Save
                </Button>
            </Modal.Footer>
        </Modal>
    );

}


{/* <Row className="mb-3">
          <Col md={12}>
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
        </Row> */}