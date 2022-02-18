import axios from 'axios'

const { REACT_APP_HOST } = process.env

const wordUrl = `http://${REACT_APP_HOST}/api/words`
const userWordsUrl = `http://${REACT_APP_HOST}/api/words/user`
const scoreUrl = `http://${REACT_APP_HOST}/api/words/userWord`

export const wordService = {
  getWords: async (user) => {
    try {
      const config = { headers: { Authorization: `Bearer ${user.token}`} }
      const response = await axios.get(`${userWordsUrl}/${user.id}`, config)
      return response
    } catch (e) {
      return e.response
    }
  },
  
  getLangWords: async (user, wordLang, langTranslation) => {
    try {
      const config = { headers: { Authorization: `Bearer ${user.token}`} }
      const response = await axios.get(
        `${userWordsUrl}/${user.id}?langWord=${wordLang}&langTranslation=${langTranslation}`,
        config
      )
      return response
    } catch (e) {
      return e.response
    }
  },

  getAllLangWords: async (user, langWord, langTranslation) => {
    try {
      const config = { headers: { Authorization: `Bearer ${user.token}`} }
      const response = await axios.get(
        `${wordUrl}?langWord=${langWord}&langTranslation=${langTranslation}`, 
        config
      )
      return response
    } catch (e) {
      return e.response
    }
  },
  
  getTrainWords: async (user, langWord, langTranslation, size) => {
    try {
      const config = { headers: { Authorization: `Bearer ${user.token}`} }
      const response = await axios.get(
        `${userWordsUrl}/${user.id}?langWord=${langWord}&langTranslation=${langTranslation}&wordNumber=${size}`,
        config
      )
      return response
    } catch (e) {
      return e.response
    }
  },

  insertWord: async (user, words) => {
    try {
      const config = { headers: { Authorization: `Bearer ${user.token}`} }
      const response = await axios.post(`${userWordsUrl}/${user.id}`, words, config)
      return response
    } catch (e) {
      return e.response
    }
  },
  
  updateWord: async (user, wordPair) => {
    try {
      const config = { headers: { Authorization: `Bearer ${user.token}`} }
      const response = await axios.put(`${userWordsUrl}/${user.id}`, wordPair, config)
      return response
    } catch (e) {
      return e.response
    }
  },

  updateScore: async (user, changes) => {
    try {
      const config = { headers: { Authorization: `Bearer ${user.token}`} }
      const response = await axios.put(`${scoreUrl}/${user.id}`, changes, config)
      return response
    } catch (e) {
      return e.response
    }
  },

  deleteWord: async (user, wordId) => {
    try {
      const config = { headers: { Authorization: `Bearer ${user.token}`} }
      const response = await axios.delete(`${userWordsUrl}/${user.id}?wordId=${wordId}`, config)
      return response
    } catch (e) {
      return e.response
    }
  }
}