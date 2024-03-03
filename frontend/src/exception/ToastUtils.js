import { toast } from 'react-toastify'

export default function ShowErrorToast (error, message) {
  const exception = error.response.data ? error.response.data : error
  toast.error(`${message}: ${exception.length > 100 ? exception.substring(0, 100) + '...' : exception}`)
  console.error(error)
}
