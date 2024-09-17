import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

function App() {
  const [services, setServices] = useState([]);
  const [serviceName, setServiceName] = useState('');
  const [comment, setComment] = useState('');

  useEffect(() => {
    fetchServices();
    const interval = setInterval(() => {
      fetchServices();
    }, 10000); // Обновляем данные каждые 10 секунд
    return () => clearInterval(interval);
  }, []);

  const fetchServices = async () => {
    try {
      const response = await axios.get('/api/services');
      console.log(JSON.stringify(response.data, null, 2)); // Логируем
      setServices(response.data);
    } catch (error) {
      console.error('Error fetching services', error);
    }
  };

  const addService = async () => {
    try {
      const response = await axios.post('/api/register', {
        serviceName,
        comment
      });

      // После успешного добавления, обновляем список сервисов
      fetchServices(); // Должен обновлять список с сервера
      setServiceName('');
      setComment('');
    } catch (error) {
      console.error('Error adding service', error);
    }
  };

  return (
      <div className="App">
        <header className="App-header">
          <h1>Service Monitoring</h1>
          <div className="form">
            <input
                type="text"
                placeholder="Service Name"
                value={serviceName}
                onChange={(e) => setServiceName(e.target.value)}
            />
            <input
                type="text"
                placeholder="Comment"
                value={comment}
                onChange={(e) => setComment(e.target.value)}
            />
            <button onClick={addService}>Add Service</button>
          </div>
          <table>
            <thead>
            <tr>
              <th>#</th>
              <th>GUID</th>
              <th>Service Name</th>
              <th>Comment</th>
              <th>Token</th>
              <th>Status</th>
            </tr>
            </thead>
            <tbody>
            {services.map((service, index) => (
                <tr key={service.guid}>
                  <td>{index + 1}</td>
                  <td>{service.guid}</td>
                  <td>{service.serviceName}</td>
                  <td>{service.comment}</td>
                  <td>{service.token}</td>
                  <td>{service.alive ? 'Alive' : 'Dead'}</td>
                </tr>
            ))}
            </tbody>
          </table>
        </header>
      </div>
  );
}

export default App;

