const form = document.querySelector('#weather-form');
const cityInput = document.querySelector('#city');
const offlineCheckbox = document.querySelector('#offline');
const statusMessage = document.querySelector('#status');
const resultArea = document.querySelector('#result');

form.addEventListener('submit', async function (event) {
  event.preventDefault();

  const city = cityInput.value.trim();
  const useOfflineSample = offlineCheckbox.checked;

  statusMessage.textContent = 'Loading forecast...';
  resultArea.replaceChildren();

  try {
    const url = `/api/weather?city=${encodeURIComponent(city)}&offline=${useOfflineSample}`;
    const response = await fetch(url, {
      headers: { Accept: 'application/json' }
    });

    if (!response.ok) {
      if (response.status === 400) {
        throw new Error('Please enter a valid city name (up to 100 characters).');
      }
      throw new Error('The forecast is unavailable. Try the offline sample.');
    }

    const weather = await response.json();
    showForecast(weather);
    statusMessage.textContent = '';
  } catch (error) {
    statusMessage.textContent = error.message || 'Could not connect to the weather service.';
  }
});

function showForecast(weather) {
  const heading = document.createElement('h2');
  heading.textContent = `${weather.city} - next 3 days`;

  const source = document.createElement('p');
  source.className = 'source';
  source.textContent = `Forecast source: ${weather.source}`;

  const table = document.createElement('table');
  const tableHead = document.createElement('thead');
  const headingRow = document.createElement('tr');
  const columnNames = ['Date', 'High', 'Low', 'Advice', 'Time window'];

  columnNames.forEach(function (name) {
    const headingCell = document.createElement('th');
    headingCell.textContent = name;
    headingRow.append(headingCell);
  });

  tableHead.append(headingRow);
  table.append(tableHead);

  const tableBody = document.createElement('tbody');
  weather.forecast.forEach(function (day) {
    const row = document.createElement('tr');
    const predictions = day.predictions || [];
    const advice = predictions.length > 0 ? predictions.join(' · ') : 'No alerts';
    const values = [
      day.date,
      `${day.highTemperature}°C`,
      `${day.lowTemperature}°C`,
      advice,
      day.timeWindow
    ];

    values.forEach(function (value) {
      const cell = document.createElement('td');
      cell.textContent = value;
      row.append(cell);
    });

    tableBody.append(row);
  });

  table.append(tableBody);
  resultArea.replaceChildren(heading, source, table);
}
