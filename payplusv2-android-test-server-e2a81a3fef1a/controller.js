//import transactions  from './transaction.js'
import fs from 'fs/promises'
class controller  {
    getTransactions = async (req, res) => {
        try {
            const list = JSON.parse(await fs.readFile('./list.json'))
            return res.send({ headers: list || [] })
        } catch (err) {
            console.log("ERROR getTransactions ", err)
            return res.send({ headers: [] })
        }
    }
    getTransactionDetails = async (req, res) => {
        const { billingId } = req.body
        console.log(billingId)
        try {
            const parseDetails = JSON.parse(await fs.readFile('./details.json'))
            const details = parseDetails.find(d => d.id == billingId)
            return res.send({ details: details || null })
        } catch(err) {
            console.log("ERROR getTransactionDetails ", err)
            return res.send({details: null})
        }
    }
    deleteTransaction = async (req, res) => {
        const { billingId } = req.body
        try {
            const list = JSON.parse(await fs.readFile('./list.json'))
            const details = JSON.parse(await fs.readFile('./details.json'))
            const indexToDeleteList = list.findIndex(item => item.id === billingId)
            if (indexToDeleteList === -1) {
                return res.send({ status: -1 })
            }
            const indexToDeleteDetails = details.findIndex(item => item.id === billingId)
            if (indexToDeleteDetails === -1) {
                return res.send({ status: -1 })
            }
            list.splice(indexToDeleteList)
            details.splice(indexToDeleteDetails)
            await fs.writeFile("./list.json",JSON.stringify(list))
            await fs.writeFile("./details.json",JSON.stringify(details))
            return res.send({ status: 0 })
        } catch (err) {
            console.log("ERROR deleteTransaction ", err)
            return res.send({ status: -1 })
        }
    }
}
export default new controller()