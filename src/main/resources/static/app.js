const form = document.querySelector('#weather-form');
const result = document.querySelector('#result');
const status = document.querySelector('#status');

form.addEventListener('submit', async event => {
  event.preventDefault();
  const city = document.querySelector('#city').value.trim();
  const offline = document.querySelector('#offline').checked;
  status.textContent = 'Loading forecast…';
  result.replaceChildren();
  try {
    const response = await fetch(`/api/weather?city=${encodeURIComponent(city)}&offline=${offline}`, {
      headers: { Accept: 'application/json' }
    });
    if (!response.ok) throw new Error(response.status === 400 ? 'Enter a city name up to 100 characters.' : 'Forecast unavailable. Try offline mode.');
    renderWeather(await response.json());
    status.textContent = '';
  } catch (error) {
    status.textContent = error.message || 'Unable to reach the service.';
  }
});

function renderWeather(data) {
  const heading = document.createElement('h2');
  heading.textContent = `${data.city} · next 3 days`;
  const source = document.createElement('p');
  source.className = 'source';
  source.textContent = `Forecast source: ${data.source}`;
  const table = document.createElement('table');
  table.innerHTML = '<thead><tr><th>Date</th><th>High</th><th>Low</th><th>Prediction</th><th>Time window</th></tr></thead>';
  const body = document.createElement('tbody');
  data.forecast.forEach(day => {
    const row = document.createElement('tr');
    [day.date, `${day.highTemperature}°C`, `${day.lowTemperature}°C`, day.predictions?.join(' · ') || 'No alerts', day.timeWindow].forEach(value => {
      const cell = document.createElement('td');
      cell.textContent = value;
      row.append(cell);
    });
    body.append(row);
  });
  table.append(body);
  result.replaceChildren(heading, source, table);
}
