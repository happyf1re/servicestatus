import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

function App() {
  const [services, setServices] = useState([]);
  const [serviceName, setServiceName] = useState('');
  const [comment, setComment] = useState('');
  const [deltaT, setDeltaT] = useState('');
  const [companyName, setCompanyName] = useState('');

  useEffect(() => {
    fetchServices();
    const interval = setInterval(() => {
      fetchServices();
    }, 10000); // Обновляем данные каждые 10 секунд
    return () => clearInterval(interval);
  }, []);

  const fetchServices = async () => {
    try {
      const response = await axios.get('https://healthcheck.a-m0.ru/api/services'); // Указываем полный URL
      console.log(JSON.stringify(response.data, null, 2)); // Логируем
      setServices(response.data);
    } catch (error) {
      console.error('Error fetching services', error);
    }
  };

  const addService = async () => {
    try {
      const response = await axios.post('https://healthcheck.a-m0.ru/api/register', { // Указываем полный URL
        serviceName,
        comment,
        deltaT: parseInt(deltaT, 10),
        companyName,
      });

      // После успешного добавления, обновляем список сервисов
      fetchServices();
      setServiceName('');
      setComment('');
      setDeltaT('');
      setCompanyName('');
    } catch (error) {
      console.error('Error adding service', error);
    }
  };

  return (
      <div className="App">
        <header className="App-header">
          <h1>Service Monitoring</h1>
          <div className="container">
            <div className="left-panel">
              <h2>Добавить новый сервис</h2>
              <div className="form">
                <input
                    type="text"
                    placeholder="Название сервиса"
                    value={serviceName}
                    onChange={(e) => setServiceName(e.target.value)}
                />
                <input
                    type="text"
                    placeholder="Название компании"
                    value={companyName}
                    onChange={(e) => setCompanyName(e.target.value)}
                />
                <input
                    type="text"
                    placeholder="Комментарий"
                    value={comment}
                    onChange={(e) => setComment(e.target.value)}
                />
                <input
                    type="number"
                    placeholder="Delta T (секунды)"
                    value={deltaT}
                    onChange={(e) => setDeltaT(e.target.value)}
                />
                <button onClick={addService}>Добавить сервис</button>
              </div>
            </div>
            <div className="right-panel">
              <h2>Статусы сервисов</h2>
              <table>
                <thead>
                <tr>
                  <th>#</th>
                  <th>GUID</th>
                  <th>Название сервиса</th>
                  <th>Компания</th>
                  <th>Комментарий</th>
                  <th>Token</th>
                  <th>Delta T (с)</th>
                  <th>Статус</th>
                  <th>Последнее обновление (GMT)</th>
                </tr>
                </thead>
                <tbody>
                {services.map((service, index) => (
                    <tr key={service.guid}>
                      <td>{index + 1}</td>
                      <td>{service.guid}</td>
                      <td>{service.serviceName}</td>
                      <td>{service.companyName}</td>
                      <td>{service.comment}</td>
                      <td>{service.token}</td>
                      <td>{service.deltaT}</td>
                      <td>{service.alive ? 'Alive' : 'Dead'}</td>
                      <td>{new Date(service.lastUpdateTime).toUTCString()}</td>
                    </tr>
                ))}
                </tbody>
              </table>
            </div>
          </div>
        </header>
      </div>
  );
}

export default App;



