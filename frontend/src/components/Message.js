import axios from "axios";
import { BACKEND_URL } from "../configuration/BackendConfig";
import { Button, Card } from "react-bootstrap";


import { useState } from 'react';

export default function Message({ message, fetchConversation }) {
    const username = localStorage.getItem("username");
    const [isEditing, setIsEditing] = useState(false);
    const [editedMessage, setEditedMessage] = useState(message.content);

    const handleDeleteComment = (commentId) => {
        // axios.delete(`${BACKEND_URL}/api/comments/delete/${commentId}`)
        //     .then(response => {
        //         fetchComments();
        //     })
        //     .catch(error => {
        //         console.error("Error deleting comment:", error);
        //         alert("Failed to delete message. Please try again.");
        //     });
    };

    const handleEditMessage = () => {
        setIsEditing(true);
    };

    const handleUpdateComment = (commentId) => {
        // axios.put(`${BACKEND_URL}/api/comments/update/${commentId}`, { content: editedComment })
        //     .then(response => {
        //         fetchComments();
        //         setIsEditing(false);
        //     })
        //     .catch(error => {
        //         console.error("Error updating comment:", error);
        //         alert("Failed to update message. Please try again.");
        //     });
    };

    return (
        <Card key={message.id} className="mb-2">
            <Card.Body>
                {isEditing ? (
                    <textarea value={editedMessage} onChange={(e) => setEditedMessage(e.target.value)} />
                ) : (
                    <Card.Text>{message.content}</Card.Text>
                )}
                <Card.Text className="text-muted">Created at: {message.createdAt}</Card.Text>
                <Card.Text className="text-muted">Author: {message.author}</Card.Text>
            </Card.Body>
            {message.author === username && (
                <Card.Footer>
                    <Button variant="danger" size="sm" onClick={() => handleDeleteComment(message.id)}>Delete</Button>
                    {isEditing ? (
                        <Button variant="success" size="sm" onClick={() => handleUpdateComment(message.id)} className="ms-2">Update</Button>
                    ) : (
                        <Button variant="primary" size="sm" onClick={handleEditMessage} className="ms-2">Edit</Button>
                    )}
                </Card.Footer>
            )}
        </Card>
    );
}