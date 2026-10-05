import React, { useState } from 'react';
import { createRoot } from 'react-dom/client';
import './style.css';

function ForecastTable({ days }) {
  return (
    <div className="table-scroll">
      <table>
        <thead>
          <tr>
            <th>Date</th>
            <th>High</th>
            <th>Low</th>
            <th>Advice</th>
            <th>Time window</th>
          </tr>
        </thead>
        <tbody>
          {days.map(function (day) {
            const advice = day.predictions && day.predictions.length > 0
              ? day.predictions.join(' · ')
              : 'No alerts';

            return (
              <tr key={day.date}>
                <td>{day.date}</td>
                <td>{day.highTemperature}°C</td>
                <td>{day.lowTemperature}°C</td>
                <td>{advice}</td>
                <td>{day.timeWindow}</td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}

function App() {
  const [city, setCity] = useState('London');
  const [days, setDays] = useState(3);
  const [offline, setOffline] = useState(false);
  const [weather, setWeather] = useState(null);
  const [message, setMessage] = useState('');

 
  async function getForecast(event) {
    event.preventDefault();
    setMessage('Loading forecast...');
    setWeather(null);

    try {
      const url = `/api/weather?city=${encodeURIComponent(city.trim())}&days=${days}&offline=${offline}`;
      const response = await fetch(url, {
        headers: { Accept: 'application/json' }
      });

      if (!response.ok) {
        if (response.status === 400) {
          throw new Error('Please enter a valid city name (up to 100 characters).');
        }
        throw new Error('The forecast is unavailable. Try the offline sample.');
      }

      setWeather(await response.json());
      setMessage('');
    } catch (error) {
      setMessage(error.message || 'Could not connect to the weather service.');
    }
  }

  function getSourceLabel(source) {
    if (source === 'offline') {
      return 'Offline sample';
    }
    return 'Live data · offline backup enabled';
  }

  return (
    <main className="page">
      <header>
        <h1>Weather Forecast</h1>
        <p className="intro">
          Choose how many days to see. Offline mode is available.
        </p>
      </header>

      <section className="panel" aria-label="Weather search">
        <form className="controls" onSubmit={getForecast}>
          <div className="city-field">
            <label htmlFor="city">City name</label>
            <input
              id="city"
              type="text"
              value={city}
              maxLength="100"
              required
              onChange={function (event) { setCity(event.target.value); }}
            />
          </div>

          <div className="days-field">
            <label htmlFor="days">Days to show</label>
            <select
              id="days"
              value={days}
              onChange={function (event) { setDays(Number(event.target.value)); }}
            >
              <option value="1">1 day</option>
              <option value="2">2 days</option>
              <option value="3">3 days</option>
              <option value="4">4 days</option>
              <option value="5">5 days</option>
            </select>
          </div>

          <label className="offline-option" htmlFor="offline">
            <input
              id="offline"
              type="checkbox"
              checked={offline}
              onChange={function (event) { setOffline(event.target.checked); }}
            />
            Use offline sample
          </label>

          <button type="submit">Get forecast</button>
        </form>

        <p className="status" role="status" aria-live="polite">{message}</p>

        {weather && (
          <section className="forecast" aria-live="polite">
            <h2>
              {weather.city} - next {weather.forecast.length}{' '}
              {weather.forecast.length === 1 ? 'day' : 'days'}
            </h2>
            <p className="source">Source: {getSourceLabel(weather.source)}</p>
            <ForecastTable days={weather.forecast} />
          </section>
        )}
      </section>

      <footer>
        API documentation: <a href="/swagger-ui/index.html">Open Swagger UI</a>
      </footer>
    </main>
  );
}

createRoot(document.getElementById('root')).render(<App />);
