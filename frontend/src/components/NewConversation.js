import axios from "axios";
import { useState } from "react";
import { BACKEND_URL } from "../configuration/BackendConfig";
import { Button, Form, Modal } from "react-bootstrap";

export default function NewConversation({ fetchConversations, show, setShow }) {

    const [name, setName] = useState("");

    const handleNewConversation = (event) => {
        event.preventDefault();
        console.log("Creating new conversation:", name);
        axios
            .post(BACKEND_URL + "/api/conversations/create", {
                name: name,
            })
            .then((response) => {
                console.log(response.data);
                fetchConversations();
            })
            .catch((error) => {
                console.error(error.response.data);
                alert("Error creating conversation!");
            });

        setName("");
        setShow(false);
    };


    return (
        <Modal show={show} onHide={() => setShow(false)}>
            <Modal.Header closeButton>
                <Modal.Title>New Post</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form onSubmit={(e) => e.preventDefault()}>
                    <Form.Group className="mb-3" controlId="name">
                        <Form.Label>Name:</Form.Label>
                        <Form.Control
                            type="text"
                            value={name}
                            onChange={(event) => setName(event.target.value)}
                        />
                    </Form.Group>
                </Form>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={() => setShow(false)}>
                    Close
                </Button>
                <Button variant="primary" onClick={handleNewConversation}>
                    Save
                </Button>
            </Modal.Footer>
        </Modal>
    );

}
