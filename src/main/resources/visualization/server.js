const express = require('express');
const fs = require('fs').promises;
const path = require('path');
const app = express();

app.use(express.json());
app.use(express.static('.'));

app.post('/saveUserGraphData', async (req, res) => {
    try {
        const userGraphData = req.body;
        await fs.writeFile(path.join(__dirname, 'userGraphData.json'), JSON.stringify(userGraphData, null, 2));
        res.status(200).send('User graph data saved successfully');
    } catch (error) {
        console.error('Error saving user graph data:', error);
        res.status(500).send('Failed to save user graph data');
    }
});

app.listen(8080, () => {
    console.log('Server running on http://localhost:8080');
});