import controller from "./controller.js"
import express from 'express'
import bodyParser from "body-parser"
const app = express();
const port = 8030;

app.use(
    bodyParser.json({
        limit: "500mb"
    })
)
app.post('/payment/billing/entry/headers', controller.getTransactions);
app.post('/payment/billing/entry/details', controller.getTransactionDetails);
app.post('/payment/billing/entry/delete', controller.deleteTransaction)

app.listen(port, () => console.log(`Express app running on port ${port}!`));