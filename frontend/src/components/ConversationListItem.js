import axios from 'axios';
import { Card, Button } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { BACKEND_URL } from '../configuration/BackendConfig';
import { ToastContainer, toast } from 'react-toastify';

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
                    <Card.Title>
                        {conversation.name}
                    </Card.Title>
                    <Card.Subtitle className="mb-2 text-muted">
                        Participants: {conversation.participants.join(', ')}
                    </Card.Subtitle>
                    <Card.Text>
                        Messages: {conversation.messages ? conversation.messages.length : 0}
                    </Card.Text>
                </Card.Body>
                <Card.Footer>
                    <Button variant="primary" className="me-2" onClick={() => navigate(`/conversation/${conversation.id}`)}>View</Button>
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