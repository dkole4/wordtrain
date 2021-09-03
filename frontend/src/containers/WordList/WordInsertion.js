import React, { useState } from 'react'
import { useSelector, useDispatch } from 'react-redux'

import { useField } from 'utils/useField'
import { setErrorMessage, setNotificationMessage } from 'store/actions/notification-actions'
import { createWords } from 'store/actions/word-actions'

import {
  Container, Table, Button, Header, Form, Dropdown
} from 'semantic-ui-react'
import { languageOptions } from '.'

export const WordInsertion = () => {
  const [word, resetWord] = useField('text')
  const [transl, resetTransl] = useField('text')
  const [wordLang, resetWordLang] = useField('dropdown')
  const [translLang, resetTranslLang] = useField('dropdown')
  const [wordList, setWordList] = useState([])

  const dispatch = useDispatch()
  const user = useSelector(state => state.loggedUser)

  const addWord = () => {
    if (transl.value && word.value && wordLang.value && translLang.value) {
      const newWord = {
        word: word.value,
        translation: transl.value,
        lang_word: wordLang.value,
        lang_translation: translLang.value
      }

      const same = wordList.filter(word => 
        word.word === newWord.word && word.transl === newWord.transl
      )

      if (same.length === 0) {
        setWordList(wordList.concat(newWord))
        dispatch(setNotificationMessage(`A new word ${word.value} was added to the list.`))
        resetWord()
        resetTransl()
      } else {
        dispatch(setErrorMessage('You already added this word'))
      }
    } else {
      dispatch(setErrorMessage('Some of the fields don\'t have any value'))
    }
  }

  const removeWord = (word, translation) => {
    setWordList(wordList.filter(w => w.word !== word && w.transl !== translation))
  }

  const insertWords = async (e) => {
    e.preventDefault()
    
    dispatch(createWords(user, wordList))
    dispatch(setNotificationMessage('Words were successfully added.'))
    
    resetWord()
    resetTransl()
    resetWordLang()
    resetTranslLang()
    setWordList([])
  }

  return (
    <Container>
      <Header>
        Add new words
      </Header>
      <Form onSubmit={insertWords}>
        <Table>
          <Table.Header>
            <Table.Row>              
              <Table.HeaderCell />
              <Table.HeaderCell><label>Word</label></Table.HeaderCell>
              <Table.HeaderCell><label>Translation</label></Table.HeaderCell>
            </Table.Row>
          </Table.Header>
          <Table.Body>

            <Table.Row>
              <Table.Cell />
              <Table.Cell>
                <Dropdown
                  {...wordLang}
                  fluid
                  placeholder='Select Language'
                  selection
                  options={languageOptions} />
              </Table.Cell>
              <Table.Cell>
                <Dropdown 
                  {...translLang}
                  fluid
                  placeholder='Select Language' 
                  selection 
                  options={languageOptions} />
              </Table.Cell>
            </Table.Row>

            <Table.Row>
              <Table.Cell>
                <Button circular icon='plus' onClick={addWord} type='button'/>
              </Table.Cell>
              <Table.Cell>
                <input placeholder='Enter word' { ...word } />
              </Table.Cell>
              <Table.Cell>
                <input placeholder='Enter translation' { ...transl } />
              </Table.Cell>
            </Table.Row>

            { 
              wordList.map(word =>
                <Table.Row key={word.word}>
                  <Table.Cell>
                    <Button 
                      circular
                      icon='minus'
                      onClick={() => removeWord(word.word, word.transl)}
                      type='button'/>
                  </Table.Cell>
                  <Table.Cell>{word.word}</Table.Cell>
                  <Table.Cell>{word.transl}</Table.Cell>
                </Table.Row>
              )
            }
                        
            <Table.Row>
              <Table.Cell colSpan='3'>
                <Button disabled={wordList.length === 0} fluid positive type='submit'>
                  Submit
                </Button>
              </Table.Cell>
            </Table.Row>
          </Table.Body>
        </Table>
      </Form>
    </Container>
  )
}