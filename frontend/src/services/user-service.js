import axios from 'axios'

const { REACT_APP_HOST } = process.env

const usersUrl = `https://${REACT_APP_HOST}/api/users`

export const userService = {
  getUsers: async (user) => {
    try {
      const config = { headers: { Authorization: `bearer ${user.token}`} }
      const response = await axios.get(usersUrl, config)
      return response
    } catch (e) {
      console.log(e.response)
      return e.response
    }
  }
}