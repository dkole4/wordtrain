import axios from 'axios'

const { REACT_APP_HOST } = process.env

const dictUrl = `http://${REACT_APP_HOST}:8081/dictionary`
const trainUrl = `http://${REACT_APP_HOST}:8081/training`
const scoreUrl = `http://${REACT_APP_HOST}:8081/user_words`
const wordUrl = `http://${REACT_APP_HOST}:8081/words`

export const wordService = {
  getWords: async (user) => {
    try {
      const config = { headers: { Authorization: `bearer ${user.token}`} }
      const response = await axios.get(`${dictUrl}/${user.id}`, config)
      return response
    } catch (e) {
      return e.response
    }
  },
  
  getLangWords: async (user, wordLang, translationLang) => {
    try {
      const payload = { lang_w: wordLang, lang_t: translationLang }
      const config = { headers: { Authorization: `bearer ${user.token}`} }
      const response = await axios.post(`${dictUrl}/${user.id}`, payload, config)
      return response
    } catch (e) {
      return e.response
    }
  },

  getAllLangWords: async (user, wordLang, translationLang) => {
    try {
      const payload = { lang_w: wordLang, lang_t: translationLang }
      const config = { headers: { Authorization: `bearer ${user.token}`} }
      const response = await axios.post(`${dictUrl}`, payload, config)
      return response
    } catch (e) {
      return e.response
    }
  },
  
  getTrainWords: async (user, wordLang, translationLang, size) => {
    try {
      const payload = { lang_w: wordLang, lang_t: translationLang, size }
      const config = { headers: { Authorization: `bearer ${user.token}`} }
      const response = await axios.post(`${trainUrl}/${user.id}`, payload, config)
      return response
    } catch (e) {
      return e.response
    }
  },

  insertWord: async (user, words) => {
    try {
      const payload = { userId: user.id, words }
      const config = { headers: { Authorization: `bearer ${user.token}`} }
      const response = await axios.post(wordUrl, payload, config)
      return response
    } catch (e) {
      return e.response
    }
  },
  
  updateWord: async (wordId, user, wordPair) => {
    try {
      const payload = { userId: user.id, ...wordPair }
      const config = { headers: { Authorization: `bearer ${user.token}`} }
      const response = await axios.put(`${wordUrl}/${wordId}`, payload, config)
      return response
    } catch (e) {
      return e.response
    }
  },

  updateScore: async (user, changes) => {
    try {
      const payload = { userId: user.id, changes }
      const config = { headers: { Authorization: `bearer ${user.token}`} }
      const response = await axios.put(`${scoreUrl}`, payload, config)
      return response
    } catch (e) {
      return e.response
    }
  },

  deleteWord: async (user, wordId) => {
    try {
      const config = { headers: { Authorization: `bearer ${user.token}`} }
      const response = await axios.delete(`${wordUrl}/${wordId}:${user.id}`, config)
      return response
    } catch (e) {
      return e.response
    }
  }
}


const loginUrl = `http://${REACT_APP_HOST}:8081/login`
const registerUrl = `http://${REACT_APP_HOST}:8081/register`

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

const usersUrl = `http://${REACT_APP_HOST}:8081/users`

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