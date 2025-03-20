import React from 'react';
import './Footer.css';

function Footer() {
  return (
    <footer className="footer">
      <div className="footer-container">
        <p>© {new Date().getFullYear()} Steam个人资料. 基于Steam API构建.</p>
        <p>
          <a href="https://steamcommunity.com/" target="_blank" rel="noopener noreferrer">
            访问Steam
          </a>
        </p>
      </div>
    </footer>
  );
}

export default Footer; 