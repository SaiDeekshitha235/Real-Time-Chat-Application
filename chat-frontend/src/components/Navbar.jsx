import React from 'react';
import { Link } from 'react-router-dom';

const Navbar = ({ user, onLogout }) => {
  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <h1>💬 Chat App</h1>
      </div>
      <div className="navbar-user">
        {user && (
          <>
            <span className="user-name">{user.name}</span>
            <button onClick={onLogout} className="btn btn-logout">
              Logout
            </button>
          </>
        )}
      </div>
    </nav>
  );
};

export default Navbar;