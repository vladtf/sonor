import axios from 'axios';
import { Card, Button } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { BACKEND_URL } from '../configuration/BackendConfig';
import { ToastContainer, toast } from 'react-toastify';
import { FaRegComments, FaRegUser, FaRegEnvelope } from 'react-icons/fa';

export default function ConversationListItem({ conversation, fetchConversations }) {

    const handleDelete = async (id) => {
        try {
            axios
                .delete(`${BACKEND_URL}/api/conversations/delete/${id}`)
                .then(response => {
                    toast.success("Conversation deleted successfully!");
                    fetchConversations();
                })
                .catch(error => {
                    console.error(error);
                    toast.error("Error deleting conversation!");
                });
        } catch (error) {
            console.error(error);
        }
    }

    const navigate = useNavigate();
    return (
        <>
            <ToastContainer />
            <Card className="mb-3 card-hover-effect" onClick={() => navigate(`/conversation/${conversation.id}`)}>
                <Card.Body>
                    <Card.Title><FaRegComments className="me-2" />{conversation.name}</Card.Title>
                    <Card.Subtitle className="mb-2 text-muted">
                        <FaRegUser className="me-1" />Participants: {conversation.participants.join(', ')}
                    </Card.Subtitle>
                    <Card.Text><FaRegEnvelope className="me-1" />Messages: {conversation.messages ? conversation.messages.length : 0}</Card.Text>
                </Card.Body>
                <Card.Footer className="d-flex justify-content-between">
                    <Button variant="primary" className="me-2" onClick={(event) => { event.stopPropagation(); navigate(`/conversation/${conversation.id}`); }}>View</Button>
                    <Button
                        variant="danger"
                        onClick={(event) => {
                            event.stopPropagation();
                            handleDelete(conversation.id);
                        }}
                    >
                        Delete
                    </Button>
                </Card.Footer>
            </Card>
        </>

    );
}