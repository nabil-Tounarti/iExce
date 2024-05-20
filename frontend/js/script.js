function uploadFile() {
    const fileInput = document.getElementById('fileInput');
    const file = fileInput.files[0];
    if (!file) {
        alert('Please select a file.');
        return;
    }

    const formData = new FormData();
    formData.append('file', file);

    fetch('http://localhost:8080/upload', {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.text();
    })
    .then(data => {
        console.log('File uploaded successfully:', data);
        displayResult(data); 
    })
    .catch(error => {
        console.error('There was a problem with the upload:', error);
        alert('There was a problem with the upload.');
    });
}

function displayResult(result) {
    const resultContainer = document.getElementById('resultContainer');
    resultContainer.textContent = 'Result from API: ' + result;
    resultContainer.style.display = 'block'
}

function downloadFile() {
    const fileHash = document.getElementById('fileHash').value;
    if (!fileHash) {
        alert('Please enter a file hash.');
        return;
    }
    console.log(fileHash)

    window.location.href = `http://localhost:8080/file/${fileHash}`;
}

function GetTheFile(fileHash) {
    if (!fileHash) {
        alert('Please enter a file hash.');
        return;
    }
    console.log(fileHash)

    window.location.href = `http://localhost:8080/file/${fileHash}`;
}


const apiUrl = 'http://localhost:3336/data';
const rowsPerPage = 5;
let currentPage = 1;
let data = [];

// Fonction asynchrone pour récupérer les données depuis l'API
async function fetchData() {
  try {
    const response = await fetch(apiUrl);
    // Vérification si la réponse est OK (statut 200-299)
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    const result = await response.json();
    // Supposons que chaque élément a un champ 'value' contenant un JSON stringifié
    data = result.map(item => JSON.parse(item.value));
    // Afficher les données et mettre à jour les contrôles de pagination
    console.log(result)
    displayData();
    updatePaginationControls();
  } catch (error) {
    console.error('Error fetching data:', error);
  }
}

// Fonction pour afficher les données sur la page actuelle
function displayData() {
  const start = (currentPage - 1) * rowsPerPage;
  const end = start + rowsPerPage;
  const currentData = data.slice(start, end);

  const tableBody = document.querySelector('#data-table tbody');
  tableBody.innerHTML = '';

  currentData.forEach(item => {
    const row = document.createElement('tr');
    row.innerHTML = `
      <td>${item.fileName}</td>
      <td>${item.hashCode}</td>
      <td><button onclick="GetTheFile('${item.hashCode}')" class="download-btn">Download File</button></td>
    `;
    tableBody.appendChild(row);
  });

  // Mise à jour de l'information de la page
  document.getElementById('page-info').textContent = `Page ${currentPage} of ${Math.ceil(data.length / rowsPerPage)}`;
}

// Fonction pour mettre à jour les contrôles de pagination (précédent/suivant)
function updatePaginationControls() {
  document.getElementById('prev-page').disabled = currentPage === 1;
  document.getElementById('next-page').disabled = currentPage === Math.ceil(data.length / rowsPerPage);
}

// Gestionnaire d'événements pour le bouton "précédent"
document.getElementById('prev-page').addEventListener('click', () => {
  if (currentPage > 1) {
    currentPage--;
    displayData();
    updatePaginationControls();
  }
});

// Gestionnaire d'événements pour le bouton "suivant"
document.getElementById('next-page').addEventListener('click', () => {
  if (currentPage < Math.ceil(data.length / rowsPerPage)) {
    currentPage++;
    displayData();
    updatePaginationControls();
  }
});

// Appel initial pour récupérer et afficher les données
fetchData();