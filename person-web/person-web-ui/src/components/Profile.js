import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './Profile.css';

function Profile() {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchProfile = async () => {
    try {
      setLoading(true);
      const response = await axios.get('/api/profile');
      setProfile(response.data);
      setError(null);
    } catch (err) {
      setError('获取用户资料失败，请稍后再试');
      console.error('获取资料错误:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleRefresh = async () => {
    try {
      setLoading(true);
      const response = await axios.post('/api/profile/refresh');
      setProfile(response.data);
      setError(null);
    } catch (err) {
      setError('刷新用户资料失败，请稍后再试');
      console.error('刷新资料错误:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProfile();
  }, []);

  if (loading) {
    return (
      <div className="profile-container loading">
        <div className="loading-spinner"></div>
        <p>加载中...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="profile-container error">
        <p>{error}</p>
        <button className="refresh-button" onClick={fetchProfile}>
          重试
        </button>
      </div>
    );
  }

  if (!profile) {
    return (
      <div className="profile-container error">
        <p>未找到用户资料</p>
        <button className="refresh-button" onClick={handleRefresh}>
          创建资料
        </button>
      </div>
    );
  }

  return (
    <div className="profile-container">
      <div className="profile-header">
        <img
          src={profile.avatarUrl || 'https://steamcdn-a.akamaihd.net/steamcommunity/public/images/avatars/default.jpg'}
          alt="用户头像"
          className="profile-avatar"
        />
        <div className="profile-info">
          <h1>{profile.steamUsername}</h1>
          <div className="status">{profile.status}</div>
          {profile.realName && <div className="real-name">真实姓名: {profile.realName}</div>}
          {profile.country && <div className="country">国家: {profile.country}</div>}
        </div>
        <button className="refresh-button" onClick={handleRefresh}>
          刷新资料
        </button>
      </div>

      <div className="profile-content">
        <div className="profile-section">
          <h2>个人介绍</h2>
          <div className="profile-summary">{profile.summary || '无个人介绍'}</div>
        </div>

        <div className="profile-section">
          <h2>最后在线</h2>
          <div>
            {profile.lastLogoff
              ? new Date(profile.lastLogoff).toLocaleString('zh-CN')
              : '未知'}
          </div>
        </div>
      </div>

      <div className="profile-footer">
        <a
          href={`https://steamcommunity.com/profiles/${profile.steamId}`}
          target="_blank"
          rel="noopener noreferrer"
          className="steam-link"
        >
          在Steam上查看完整资料
        </a>
      </div>
    </div>
  );
}

export default Profile; 