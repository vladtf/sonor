import "./App.css";
import React, { useState } from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.min.js";
import { BrowserRouter, Route, Link, Routes } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import RegistrationPage from "./pages/RegistrationPage";
import HomePage from "./pages/HomePage";
import { Navigate } from "react-router-dom";
import PostsPage from "./pages/PostsPage.js";
import ConversationsPage from "./pages/ConversationsPage.js";
import PostPage from "./pages/PostDetailPage.js";
import MyNavbar from "./components/MyNavbar";
import PostDetailPage from "./pages/PostDetailPage.js";
import ConversationDetailPage from "./pages/ConversationDetailPage.js";
import 'react-toastify/dist/ReactToastify.css';
import FeedbackPage from "./pages/FeedbackPage.js";
import AdminPage from "./pages/AdminPage.js";

function App() {
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
  );
}

export default App;
