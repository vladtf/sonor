import axios from "axios";
import { useEffect, useState } from "react";
import { Button, Form, Modal, Spinner } from "react-bootstrap";
import { ToastContainer, toast } from "react-toastify";
import { BACKEND_URL } from "../configuration/BackendConfig";

export default function NewParticipant({ fetchConversation, conversationId, show, setShow }) {

    const [username, setUsername] = useState("");
    const [usernames, setUsernames] = useState([]);
    const [loadingUsernames, setLoadingUsernames] = useState(true);

    useEffect(() => {
        fetchUsernames();
    }, []);

    const fetchUsernames = async () => {
        setLoadingUsernames(true);
        try {
            axios
                .get(BACKEND_URL + "/api/users/usernames")
                .then((response) => {
                    console.log(response.data);
                    setUsernames(response.data);
                })
                .catch((error) => {
                    console.error(error.response.data);
                    toast.error("Error retrieving usernames!");
                })
                .finally(() => {
                    setLoadingUsernames(false);
                });
        } catch (error) {
            console.error(error.response.data);
        }
    }

    const handleNewParticipant = (event) => {
        event.preventDefault();

        if (username === "") {
            toast.warning("Please select a participant!");
            return;
        }

        console.log("Adding participant: " + username);
        axios
            .post(BACKEND_URL + "/api/conversations/addUser", {
                conversationId: conversationId,
                username: username
            })
            .then((response) => {
                console.log(response.data);
                fetchConversation(conversationId);
            })
            .catch((error) => {
                console.error(error.response.data);
                toast.error("Failed to add participant. Please try again.");
            });

        setUsername("");
        setShow(false);
    };


    return (
        <>
            <ToastContainer />
            <Modal show={show} onHide={() => setShow(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Add Participant</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form onSubmit={(e) => e.preventDefault()}>
                        <Form.Group className="mb-3" controlId="name">
                            <Form.Label>Name: {loadingUsernames ? <Spinner animation="border" size="sm" /> : ""}</Form.Label>
                            <Form.Select onChange={(e) => setUsername(e.target.value)} required>
                                <option value="">Select a participant</option>
                                {usernames.map((username, index) => (
                                    <option key={index} value={username}>{username}</option>
                                ))}
                            </Form.Select>
                        </Form.Group>
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShow(false)}>
                        Close
                    </Button>
                    <Button variant="primary" onClick={handleNewParticipant}>
                        Save
                    </Button>
                </Modal.Footer>
            </Modal>
        </>

    );

}
