import axios from 'axios'
import { useEffect, useState } from 'react'
import { Button, Card, Col, Container, Form, Pagination, Row } from 'react-bootstrap'
import { FaRegUser } from 'react-icons/fa'
import { useNavigate } from 'react-router-dom'
import { ToastContainer, toast } from 'react-toastify'
import { BACKEND_URL } from '../configuration/BackendConfig'
import ShowErrorToast from '../exception/ToastUtils'

function AdminPage () {
  const [users, setUsers] = useState([])
  const [currentPage, setCurrentPage] = useState(0)
  const [searchTerm, setSearchTerm] = useState('')

  const token = localStorage.getItem('jwtToken')
  const navigate = useNavigate()
  useEffect(() => {
    if (!token) {
      delete axios.defaults.headers.common.Authorization
      navigate('/login')
    } else {
      axios.defaults.headers.common.Authorization = token
    }
  }, [token, navigate])

  useEffect(() => {
    fetchData()
  }, [])

  const handleSearch = (pageNumber = 0, pageSize = 5) => {
    axios
      .get(`${BACKEND_URL}/api/users/search`, {
        params: {
          searchTerm,
          page: pageNumber,
          size: pageSize
        }
      })
      .then((response) => {
        console.log(response.data)
        setUsers(response.data)
      })
      .catch((error) => {
        ShowErrorToast(error, 'Error retrieving users!')
      })
  }

  const fetchData = (pageNumber = 0, pageSize = 3) => {
    axios
      .get(`${BACKEND_URL}/api/users/all`, {
        params: {
          page: pageNumber,
          size: pageSize
        }
      })
      .then((response) => {
        console.log(response.data)
        setUsers(response.data)
      })
      .catch((error) => {
        ShowErrorToast(error, 'Error retrieving users!')
      })
  }

  const handleDelete = (id) => {
    const confirmDelete = window.confirm(
      'Are you sure you want to delete this user?'
    )

    if (!confirmDelete) {
      return
    }

    const deleteRequest = {
      id
    }

    axios
      .delete(`${BACKEND_URL}/api/users/delete/${id}`, { data: deleteRequest })
      .then((response) => {
        console.log(response.data)
        toast.success('User deleted successfully!')
        fetchData()
      })
      .catch((error) => {
        ShowErrorToast(error, 'Failed to delete user.')
      })
  }

  const handlePageChange = (pageNumber) => {
    if (pageNumber < 0 || pageNumber > users.totalPages + 1) {
      return
    }

    setCurrentPage(pageNumber)
    fetchData(pageNumber - 1)
  }

  return (
    <>
      <ToastContainer />
      <Container className="w-50">
        <hr />
        <Row>
          <Col md={6}>
            <Form.Control
              type="text"
              placeholder="Search users"
              value={searchTerm}
              onChange={(event) => setSearchTerm(event.target.value)}
              onKeyUp={(event) => {
                if (event.key === 'Enter') {
                  handleSearch(0, 5)
                }
              }
              }
            />
          </Col>
          <Col md={1}>
            <Button variant="primary" onClick={() => handleSearch(0, 5)} disabled={searchTerm.length === 0}>
              Search
            </Button>
          </Col>
          <Col md={1}>
            <Button variant="secondary" onClick={() => { setSearchTerm(''); fetchData(0, 5) }}>
              Reset
            </Button>
          </Col>
          <Col md={4} className="d-flex justify-content-end">
            <Pagination className="mb-0">
              <Pagination.Prev onClick={() => handlePageChange(currentPage - 1)} disabled={currentPage === 1} />
              {[...Array(users.totalPages).keys()].map(pageNumber => (
                <Pagination.Item key={pageNumber + 1} active={pageNumber + 1 === currentPage} onClick={() => handlePageChange(pageNumber + 1)}>
                  {pageNumber + 1}
                </Pagination.Item>
              ))}
              <Pagination.Next onClick={() => handlePageChange(currentPage + 1)} disabled={currentPage === users.totalPages} />
            </Pagination>
          </Col>
        </Row>
        <hr />
        <Row>
          {users.content && users.content.length === 0
            ? (
            <h2 className="text-center">No users found</h2>
              )
            : (
                users.content && users.content.map((user, index) => {
                  return (
                <Col md="12" key={index} className="p-2">
                  <Card className="card-hover-effect">
                    <Card.Body>
                      <Card.Title><FaRegUser className="me-2" />{user.username}</Card.Title>
                      <Card.Subtitle className="mb-2 text-muted">
                        Roles: {user.roles.join(', ')}
                      </Card.Subtitle>
                      <Card.Text>Posts: {user.postCount}</Card.Text>
                      <Card.Text>Comments: {user.commentCount}</Card.Text>
                      <Card.Text>Messages: {user.messageCount || 0}</Card.Text>
                    </Card.Body>
                    <Card.Footer className="text-muted d-flex justify-content-end">
                      <Button
                        variant="danger"
                        onClick={(event) => {
                          event.stopPropagation()
                          handleDelete(user.id)
                        }}
                      >
                        Delete User
                      </Button>
                    </Card.Footer>
                  </Card>
                </Col>
                  )
                })
              )}
        </Row>
        <hr />
      </Container>
    </>
  )
}

export default AdminPage
