import { Col, Container, Row } from "react-bootstrap";
import MyNavbar from "../components/MyNavbar";
import { useState, useEffect } from "react";
import axios from "axios";
import { BACKEND_URL } from "../configuration/BackendConfig";
import { useNavigate } from "react-router-dom";

function TransactionPage() {
  const [ibanSource, setIbanSource] = useState("");
  const [ibanDest, setIbanDest] = useState("");
  const [amount, setAmount] = useState("");
  const [accounts, setAccounts] = useState([]);

  const [transactions, setTransactions] = useState([]);
  const [selectedAccount, setSelectedAccount] = useState("");

  const token = localStorage.getItem("jwtToken");
  const navigate = useNavigate();
  useEffect(() => {
    if (!token) {
      delete axios.defaults.headers.common["Authorization"];
      navigate("/login");
    } else {
      axios.defaults.headers.common["Authorization"] = token;
    }
  }, [token, navigate]);

  useEffect(() => {
    getMyAccounts();
    fetchTransactions();
  }, []);

  const getMyAccounts = () => {

    axios
      .get(BACKEND_URL + "/api/accounts")
      .then((response) => {
        console.log(response.data);
        setAccounts(response.data);
      })
      .catch((error) => {
        alert("Error retrieving accounts!");
        console.error(error.response.data);
      });
  };

  const fetchTransactions = async () => {
    try {
      const response = await axios.get(BACKEND_URL + "/api/transactions");

      console.log(response.data);
      setTransactions(response.data);
    } catch (error) {
      console.error(error.response.data);
    }
  };

  const updateTransactions = async (event) => {
    const iban = event.target.value;
    setSelectedAccount(iban);

    try {
      const response = await axios.get(
        BACKEND_URL + `/api/transactions${iban ? `?iban=${iban}` : ""}`
      );
      console.log(response.data);
      setTransactions(response.data);
    } catch (error) {
      console.error(error.response.data);
    }
  };

  const sendTransaction = () => {
    const transactionRequest = {
      ibanSource: ibanSource,
      ibanDest: ibanDest,
      sum: amount,
    };

    const confirmTransaction = window.confirm(
      `Are you sure you want to send ${amount} from ${ibanSource} to ${ibanDest}?`
    );

    if (!confirmTransaction) {
      return;
    }

    console.log("Sending transaction data: ", transactionRequest);

    axios
      .post(BACKEND_URL + "/api/transaction", transactionRequest)
      .then((response) => {
        console.log(response.data);
        alert("Transaction successful!");
        fetchTransactions(); // Fetch transactions again after successful transaction
      })
      .catch((error) => {
        alert("Error sending transaction!");
        console.error(error.response.data);
      });
  };

  return (
    <>
      <Container>
        <MyNavbar />
        <Row>
          <Col md={6}>
            <div className="mb-3">
              <label htmlFor="ibanSource" className="form-label">
                My account IBAN
              </label>
              <select
                className="form-select"
                id="ibanSource"
                onChange={(e) => setIbanSource(e.target.value)}
                value={ibanSource}
              >
                <option value="">Select an account</option>
                {accounts.map((account) => (
                  <option key={account.iban} value={account.iban}>
                    {account.iban}
                  </option>
                ))}
              </select>
            </div>
            <div className="mb-3">
              <label htmlFor="ibanDest" className="form-label">
                Destination account IBAN
              </label>
              <select
                className="form-select"
                id="ibanDest"
                onChange={(e) => setIbanDest(e.target.value)}
                value={ibanDest}
              >
                <option value="">Select an account</option>
                {accounts.map((account) => (
                  <option key={account.iban} value={account.iban}>
                    {account.iban}
                  </option>
                ))}
              </select>
            </div>
            <div className="mb-3">
              <label htmlFor="amount" className="form-label">
                Amount
              </label>
              <input
                type="number"
                className="form-control"
                id="amount"
                placeholder="Enter amount"
                value={amount}
                onChange={(event) => setAmount(event.target.value)}
              />
            </div>
            <div className="mb-3">
              <button className="btn btn-primary" onClick={sendTransaction}>
                Send Transaction
              </button>
            </div>
          </Col>
        </Row>

        <hr />

        <Row className="mb-3">
          <Col md={6}>
            <h3>My Transactions</h3>
            <div className="form-group">
              <label htmlFor="ibanSource">IBAN Source</label>
              <select
                className="form-select"
                id="ibanSource"
                onChange={(e) => updateTransactions(e)}
                value={selectedAccount}
              >
                <option value="">All Accounts</option>
                {accounts.map((account) => (
                  <option key={account.id} value={account.iban}>
                    {account.iban}
                  </option>
                ))}
              </select>
            </div>
          </Col>
        </Row>

        <Row>
          <Col>
            <table className="table table-striped">
              <thead>
                <tr>
                  <th scope="col">IBAN Source</th>
                  <th scope="col">IBAN Dest</th>
                  <th scope="col">Amount</th>
                  <th scope="col">Transaction Type</th>
                  <th scope="col">Date</th>
                </tr>
              </thead>
              <tbody>
                {transactions.map((transaction) => (
                  <tr
                    key={transaction.id}
                    className={
                      transaction.transactionType === "INCOME"
                        ? "table-success"
                        : "table-danger"
                    }
                  >
                    <td>{transaction.sourceAccount}</td>
                    <td>{transaction.destAccount}</td>
                    <td>{transaction.sum}</td>
                    <td>{transaction.transactionType}</td>
                    <td>{transaction.createdAt}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </Col>
        </Row>
      </Container>
    </>
  );
}

export default TransactionPage;
