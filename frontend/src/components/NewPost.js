import axios from "axios";
import { useState } from "react";
import { Button, Form, Modal } from "react-bootstrap";
import { ToastContainer, toast } from "react-toastify";
import { BACKEND_URL } from "../configuration/BackendConfig";

export default function NewPost({ fetchPosts, show, setShow }) {
    const [title, setTitle] = useState("");
    const [category, setCategory] = useState("OTHER");
    const [content, setContent] = useState("");


    const handleNewPost = (e) => {
        // check if the title and content are empty
        if (title === "" || content === "") {
            toast.warning("Title and content are required!");
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
                toast.error("Failed to create post. Please try again.");
                console.error(error.response.data);
            });

        // clear the form
        setTitle("");
        setCategory("");
        setContent("");
        setShow(false);
    };

    return (
        <>
            <ToastContainer />
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
        </>

    );

}

