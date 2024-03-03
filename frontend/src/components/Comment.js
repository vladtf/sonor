import axios from 'axios'
import { useState } from 'react'
import { Button, Card } from 'react-bootstrap'
import { ToastContainer, toast } from 'react-toastify'
import { BACKEND_URL } from '../configuration/BackendConfig'

export default function Comment({ comment, fetchComments }) {
  const username = localStorage.getItem('username')
  const [isEditing, setIsEditing] = useState(false)
  const [editedComment, setEditedComment] = useState(comment.content)

  const handleDeleteComment = (commentId) => {
    axios.delete(`${BACKEND_URL}/api/comments/delete/${commentId}`)
      .then(response => {
        fetchComments()
      })
      .catch(error => {
        console.error('Error deleting comment:', error)
        toast.error('Failed to delete comment. Please try again.')
      })
  }

  const handleEditComment = () => {
    setIsEditing(true)
  }

  const handleUpdateComment = (commentId) => {
    axios.put(`${BACKEND_URL}/api/comments/update/${commentId}`, { content: editedComment })
      .then(response => {
        fetchComments()
        setIsEditing(false)
      })
      .catch(error => {
        console.error('Error updating comment:', error)
        toast.error('Failed to update comment. Please try again.')
      })
  }

  return (
    <>
      <ToastContainer />
      <Card key={comment.id} className="mb-2">
        <Card.Body>
          {isEditing
            ? (
              <textarea value={editedComment} onChange={(e) => setEditedComment(e.target.value)} />
            )
            : (
              <Card.Text>{comment.content}</Card.Text>
            )}
          <Card.Text className="text-muted">Created at: {comment.createdAt}</Card.Text>
          <Card.Text className="text-muted">Author: {comment.author}</Card.Text>
        </Card.Body>
        {comment.author === username && (
          <Card.Footer>
            <Button variant="danger" size="sm" onClick={() => handleDeleteComment(comment.id)}>Delete</Button>
            {isEditing
              ? (
                <Button variant="success" size="sm" onClick={() => handleUpdateComment(comment.id)} className="ms-2">Update</Button>
              )
              : (
                <Button variant="primary" size="sm" onClick={handleEditComment} className="ms-2">Edit</Button>
              )}
          </Card.Footer>
        )}
      </Card>
    </>

  )
}
