import React, { useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { Container, Segment, Table } from 'semantic-ui-react'

import { logout } from 'store/actions/auth-actions'
import { createWords } from 'store/actions/word-actions'
import { wordService } from 'services/word-service'
import { WordSearchBar } from './WordSearchBar'
import { WordPair } from './WordPair'


export const WordSearch = () => {
  const [words, setWords] = useState([])
  const user = useSelector(state => state.loggedUser)
  const userWords = useSelector(state => state.words)
  
  const dispatch = useDispatch()

  const getLanguageWords = async (wordLang, transLang) => {
    const response = await wordService.getAllLangWords(user, wordLang, transLang)

    if (response.status === 200) {
      setWords(response.data)
    } else {
      dispatch(logout())
    }
  }

  const addWord = async (wordPair) => {
    const word = {
      langWord: wordPair.langWord,
      langTranslation: wordPair.langTranslation,
      word: wordPair.word,
      translation: wordPair.translation
    }

    dispatch(createWords(user, [word]))
  }

  const isAdded = (wordPair) => 
    userWords.filter(word => word.id === wordPair.id).length > 0

  return (
    <Container>
      <Segment>
        <WordSearchBar getLanguageWords={getLanguageWords} />
      </Segment>
      
      <Table>
        <Table.Header>
          <Table.Row>
            <Table.HeaderCell>Word</Table.HeaderCell>
            <Table.HeaderCell>Translation</Table.HeaderCell>
            <Table.HeaderCell>Languages</Table.HeaderCell>
            <Table.HeaderCell />
          </Table.Row>
        </Table.Header>
        
        <Table.Body>
          { words.map(word => 
            <WordPair
              key={word.id}
              added={isAdded(word)}
              wordPair={word}
              addWord={addWord} />
          ) }
        </Table.Body>
      </Table>
    </Container>
  )
}