import React from 'react'
import PropTypes from 'prop-types'
import { useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { Button, Container, Dropdown, Segment, Table } from 'semantic-ui-react'

import { logout } from '../reducer-user'
import { createWords } from '../reducer-word'
import { wordService } from '../services'
import { useField } from './WordApp'
import { languageOptions } from './WordList'


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
      lang_w: wordPair.lang_word,
      lang_t: wordPair.lang_translation,
      word: wordPair.word,
      transl: wordPair.translation,
      id: wordPair.id
    }

    dispatch(createWords(user, [word]))
  }

  const isAdded = (wordPair) => {
    console.log(userWords)
    console.log(wordPair)
    return userWords.filter(word => word.id === wordPair.id).length > 0
  }

  return (
    <Container>
      <Segment>
        <SearchBar getLanguageWords={getLanguageWords} />
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
          { words.map(word => (
            <WordPair key={word.id} added={isAdded(word)} wordPair={word} addWord={addWord} />
          )) }
        </Table.Body>
      </Table>
    </Container>
  )
}

const SearchBar = ({ getLanguageWords }) => {
  const [wordLang] = useField('dropdown', '', false)
  const [transLang] = useField('dropdown', '', false)

  return (
    <Table>
      <Table.Header>
        <Table.Row>
          <Table.HeaderCell>
            Translation language
          </Table.HeaderCell>
          <Table.HeaderCell>
            Training language
          </Table.HeaderCell>
          <Table.HeaderCell />
        </Table.Row>

        <Table.Row>
          <Table.HeaderCell>
            <Dropdown fluid {...wordLang} placeholder='Select Language' selection options={languageOptions} />
          </Table.HeaderCell>
          <Table.HeaderCell>
            <Dropdown fluid {...transLang} placeholder='Select Language' selection options={languageOptions} />
          </Table.HeaderCell>
          <Table.HeaderCell>
            <Button
              positive
              onClick={() => getLanguageWords(wordLang.value, transLang.value)}
              content='Start' />
          </Table.HeaderCell>
        </Table.Row>
      </Table.Header>
    </Table>
  )
}

SearchBar.propTypes = {
  getLanguageWords: PropTypes.func.isRequired
}

const WordPair = ({ wordPair, added, addWord }) => {

  return (
    <Table.Row>
      <Table.Cell>
        { wordPair.word }
      </Table.Cell>
      <Table.Cell>
        { wordPair.translation }
      </Table.Cell>
      <Table.Cell>
        { wordPair.lang_word} / { wordPair.lang_translation }
      </Table.Cell>
      <Table.Cell collapsing textAlign='right'>
        <Button positive={!added} disabled={added} onClick={() => addWord(wordPair)}>
          { added ? 'Added' : 'Add' }
        </Button>
      </Table.Cell>
    </Table.Row>
  )
}

WordPair.propTypes = {
  wordPair: PropTypes.object.isRequired,
  added: PropTypes.bool.isRequired,
  addWord: PropTypes.func.isRequired
}