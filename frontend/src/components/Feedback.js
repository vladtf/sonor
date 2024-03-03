import axios from 'axios'
import { Button, Card } from 'react-bootstrap'
import { FaClock, FaStar, FaUser } from 'react-icons/fa'
import { ToastContainer, toast } from 'react-toastify'
import { BACKEND_URL } from '../configuration/BackendConfig'

export default function Feedback ({ feedback, fetchFeedbacks }) {
  const username = localStorage.getItem('username')

  const handleDeleteFeedback = (feedbackId) => {
    axios.delete(`${BACKEND_URL}/api/feedbacks/delete/${feedbackId}`)
      .then(response => {
        toast.success('Feedback deleted successfully')
        fetchFeedbacks()
      })
      .catch(error => {
        console.error('Error deleting feedvack:', error)
        toast.error('Failed to delete feedback. Please try again.')
      })
  }

  return (
        <>
            <ToastContainer />
            <Card key={feedback.id} className="mb-3">
                <Card.Header>
                    <Card.Title><FaStar className="me-2" />{feedback.satisfaction}</Card.Title>
                    <Card.Subtitle className="mb-2 text-muted">{feedback.feature}</Card.Subtitle>
                </Card.Header>
                <Card.Body>
                    <Card.Text>{feedback.content}</Card.Text>
                    <Card.Text className="text-muted"><FaClock className="me-2" />Created at: {feedback.createdAt}</Card.Text>
                    <Card.Text className="text-muted"><FaUser className="me-2" />Author: {feedback.username}</Card.Text>
                </Card.Body>
                {feedback.username === username && (
                    <Card.Footer>
                        <Button variant="danger" size="sm" onClick={() => handleDeleteFeedback(feedback.id)}>Delete</Button>
                    </Card.Footer>
                )}
            </Card>
        </>
  )
}
