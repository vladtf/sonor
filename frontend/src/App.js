import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.min.js'
import React from 'react'
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import 'react-toastify/dist/ReactToastify.css'
import './App.css'
import MyNavbar from './components/MyNavbar'
import AdminPage from './pages/AdminPage.js'
import ConversationDetailPage from './pages/ConversationDetailPage.js'
import ConversationsPage from './pages/ConversationsPage.js'
import FeedbackPage from './pages/FeedbackPage.js'
import HomePage from './pages/HomePage'
import LoginPage from './pages/LoginPage'
import PostDetailPage from './pages/PostDetailPage.js'
import PostsPage from './pages/PostsPage.js'
import RegistrationPage from './pages/RegistrationPage'

function App () {
  return (
    <BrowserRouter>
      <MyNavbar />
      <Routes>
        {/* <Route exact path="/" component={Home} /> */}
        <Route exact path="/" element={<Navigate to="/login" />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/registration" element={<RegistrationPage />} />
        <Route path="/home" element={<HomePage />} />
        <Route path="/posts" element={<PostsPage />} />
        <Route path="/post/:postId" element={<PostDetailPage />} />
        <Route path="/conversations" element={<ConversationsPage />} />
        <Route path="/conversation/:conversationId" element={<ConversationDetailPage />} />
        <Route path="/feedbacks" element={<FeedbackPage />} />
        <Route path="/admin" element={<AdminPage />} />
        <Route path="/*" element={<h1>Not Found</h1>} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
