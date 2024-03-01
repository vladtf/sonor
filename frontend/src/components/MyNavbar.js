import React from "react";
import { Nav, Navbar, NavDropdown } from "react-bootstrap";
import { FaCloud } from "react-icons/fa"; // Import the piggy bank icon from react-icons
import axios from "axios";
import { BACKEND_URL } from "../configuration/BackendConfig";
import { useNavigate } from "react-router-dom";

const MyNavbar = () => {
  const email = localStorage.getItem("email");
  const jwtToken = localStorage.getItem("jwtToken");
  const roles = localStorage.getItem("roles");

  const currentPage = window.location.pathname;

  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("jwtToken");
    localStorage.removeItem("email");

    // send a get request to the server to invalidate the token
    axios
      .get(BACKEND_URL + "/logout")
      .then((response) => {
        console.log(response.data);
        navigate("/login");
      })
      .catch((error) => {
        console.log(error);
      }
      );
  };

  const dropdownContainerStyle = {
    marginLeft: "auto",
  };

  const brandStyle = {
    display: "flex",
    alignItems: "center",
    fontWeight: "bold",
  };

  const navLinkStyle = {
    marginRight: "10px",
  };

  const dropdownItemStyle = {
    fontSize: "14px",
  };

  return (
    <Navbar
      expand="lg"
      variant="dark"
      bg="primary"
      style={{
        borderBottomLeftRadius: "5px",
        borderBottomRightRadius: "5px",
      }}
      className="mb-4"
    >
      <Navbar.Brand href="/home" style={brandStyle}>
        <FaCloud style={{ marginRight: "5px", marginLeft: "10px" }} /> Sonor
      </Navbar.Brand>
      <Navbar.Toggle aria-controls="basic-navbar-nav" />
      <Navbar.Collapse id="basic-navbar-nav">
        <Nav className="ml-auto">
          {!jwtToken ? (
            <>
              <Nav.Link href="/registration" style={navLinkStyle}>
                Registration
              </Nav.Link>
              <Nav.Link href="/login" style={navLinkStyle}>
                Login
              </Nav.Link>
            </>
          ) : (
            <>
              <Nav.Link href="/home" style={{ ...navLinkStyle, fontWeight: currentPage === "/home" ? "bold" : "normal" }}>
                Home
              </Nav.Link>
              <Nav.Link href="/posts" style={{ ...navLinkStyle, fontWeight: currentPage === "/posts" ? "bold" : "normal" }}>
                Posts
              </Nav.Link>
              <Nav.Link href="/transactions" style={{ ...navLinkStyle, fontWeight: currentPage === "/transactions" ? "bold" : "normal" }}>
                Transactions
              </Nav.Link>
            </>
          )}
        </Nav>
        <Nav style={dropdownContainerStyle}>
          {roles.includes("ADMIN") && jwtToken && (
            <Nav.Link href="/admin" style={navLinkStyle}>
              Admin
            </Nav.Link>
          )}
          <NavDropdown
            title="Profile"
            id="profile-nav-dropdown"
            style={navLinkStyle}
          >
            <NavDropdown.Item onClick={() => { }} style={dropdownItemStyle}>
              Change Password
            </NavDropdown.Item>
            <NavDropdown.Item
              onClick={handleLogout}
              style={dropdownItemStyle}
            >
              Logout
            </NavDropdown.Item>
          </NavDropdown>
        </Nav>
      </Navbar.Collapse>
    </Navbar>
  );
};

export default MyNavbar;
