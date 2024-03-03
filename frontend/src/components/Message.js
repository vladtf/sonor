import axios from 'axios'
import { Button, Card } from 'react-bootstrap'
import { ToastContainer, toast } from 'react-toastify'
import { BACKEND_URL } from '../configuration/BackendConfig'
import ShowErrorToast from '../exception/ToastUtils'

export default function Message({ message, fetchConversation }) {
  const username = localStorage.getItem('username')

  const handleDeleteMessage = (messageId) => {

    axios.delete(`${BACKEND_URL}/api/messages/delete/${messageId}`)
      .then(response => {
        toast.success('Message deleted successfully')
        fetchConversation()
      })
      .catch(error => {
        ShowErrorToast(error, 'Failed to delete message.')
      })
  }

  return (
    <>
      <ToastContainer />
      <Card key={message.id} className="mb-2">
        <Card.Body>
          <Card.Text>{message.content}</Card.Text>
          <Card.Text className="text-muted">Created at: {message.createdAt}</Card.Text>
          <Card.Text className="text-muted">Author: {message.author}</Card.Text>
        </Card.Body>
        {message.author === username && (
          <Card.Footer>
            <Button variant="danger" size="sm" onClick={() => handleDeleteMessage(message.id)}>Delete</Button>
          </Card.Footer>
        )}
      </Card>
    </>

  )
}
