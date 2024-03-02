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
import MessagesPage from "./pages/MessagesPage.js";
import AdminPage from "./pages/AdminPage.js";
import PostPage from "./pages/PostDetailPage.js";
import MyNavbar from "./components/MyNavbar";
import PostDetailPage from "./pages/PostDetailPage.js";
import ConversationPage from "./pages/ConversationPage.js";
import 'react-toastify/dist/ReactToastify.css';

function App() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleSubmit = (event) => {
    event.preventDefault();
    console.log("Email:", email, "Password:", password);
  };

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
        <Route path="/messages" element={<MessagesPage />} />
        <Route path="/conversation/:conversationId" element={<ConversationPage />} />
        <Route path="/admin" element={<AdminPage />} />
        <Route path="/*" element={<h1>Not Found</h1>} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
