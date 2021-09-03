import axios from 'axios'

const { REACT_APP_HOST } = process.env

const loginUrl = `https://${REACT_APP_HOST}/api/login`
const registerUrl = `https://${REACT_APP_HOST}/api/register`

export const authService = {
  auth: async (payload) => {
    try {
      const response = await axios.post(loginUrl, payload)
      return response
    } catch (e) {
      return e.response
    }
  },

  register: async (payload) => {
    try {
      const response = await axios.post(registerUrl, payload)
      return response
    } catch (e) {
      return e.response
    }
  }
}