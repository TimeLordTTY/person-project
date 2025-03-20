import React from 'react';
import { Link } from 'react-router-dom';
import './Header.css';

function Header() {
  return (
    <header className="header">
      <div className="header-container">
        <Link to="/" className="logo">
          Steam个人资料
        </Link>
        <nav>
          <ul>
            <li>
              <Link to="/">首页</Link>
            </li>
          </ul>
        </nav>
      </div>
    </header>
  );
}

export default Header; 