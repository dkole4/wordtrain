import React, { useState } from 'react'
import { useSelector, useDispatch } from 'react-redux'

import { useField } from 'utils/useField'
import { setErrorMessage, setNotificationMessage } from 'store/actions/notification-actions'
import { createWords } from 'store/actions/word-actions'

import {
  Container, Table, Button, Header, Form, Dropdown, Segment
} from 'semantic-ui-react'
import { languageOptions } from '.'

import { RegularInsertionForm } from './RegularInsertionForm'
import { FreeInsertionForm } from './FreeInsertionForm'


export const WordInsertion = () => {
  const [langWord, resetLangWord] = useField('dropdown')
  const [langTransl, resetLangTransl] = useField('dropdown')
  const [wordList, setWordList] = useState([])
  const [freeForm, setFreeForm] = useState(false)
  const [invertLanguages, setInvertLanguages] = useState(false)

  const dispatch = useDispatch()
  const user = useSelector(state => state.loggedUser)
  const words = useSelector(state => state.words)

  const addWord = async (word, translation) => {
    if (wordList.length >= 10) {
      dispatch(
        setErrorMessage(
          `Word limit of 10 has been reached. 
           Submit already written words before adding new ones.`
        )
      )
    }
    else if (translation && word && langWord.value && langTransl.value) {
      const newWord = {
        word: word,
        translation: translation,
        langWord: langWord.value,
        langTranslation: langTransl.value
      }

      const same = wordList.filter(w => 
        w.word === newWord.word && w.transl === newWord.translation
      )

      if (same.length === 0) {
        setWordList(wordList.concat(newWord))
        dispatch(setNotificationMessage(`A new word ${word} was added to the list.`))
      } else {
        dispatch(setErrorMessage('You already added this word'))
      }
    } else {
      dispatch(setErrorMessage('Some of the fields don\'t have any value'))
    }
  }

  const addMany = (words) => {
    if (words.length >= 10) {
      dispatch(
        setErrorMessage(
          `Word limit of 10 has been reached. 
           Submit already written words before adding new ones.`
        )
      )
    } else {
      setWordList(
        words.map(word => {
          return {
            ...word,
            langWord: invertLanguages ? langTransl : langWord.value,
            langTranslation: invertLanguages ? langWord : langTransl.value
          }
        })
      )
      dispatch(setNotificationMessage('Words were added to the list.'))
    }
  }

  const removeWord = (word, translation) => {
    setWordList(wordList.filter(w => w.word !== word && w.transl !== translation))
  }

  const insertWords = async (e) => {
    e.preventDefault()

    const checked = wordList.filter(
      listed => words.find(
        userWord => 
          (listed.word === userWord.word && listed.translation === userWord.translation)
          || (listed.word === userWord.translation && listed.translation === userWord.word)
      ) === undefined
    )

    dispatch(createWords(user, checked))
    resetLangWord()
    resetLangTransl()
    setWordList([])

    if (checked.length !== wordList.length) {
      dispatch(
        setErrorMessage(
          `You tried to add words that are already in your wordlist.
          Duplicates were removed and all the remaining words were successfully added.`
        )
      )
    } else {
      dispatch(setNotificationMessage('Words were successfully added.'))
    }
  }

  return (
    <Container>
      <Header>
        Add new words
      </Header>
      <Segment>
        <Button fluid onClick={() => setFreeForm(!freeForm)}>
          { freeForm ? 'Use Free Form' : 'Use Regular Form' }
        </Button>
      </Segment>  

      <Form onSubmit={insertWords}>

        <Table unstackable>
          <Table.Header>
            <Table.Row>
              <Table.HeaderCell width={1}/>
              <Table.HeaderCell width={3}><label>Word</label></Table.HeaderCell>
              <Table.HeaderCell width={1}/>
              <Table.HeaderCell width={3}><label>Translation</label></Table.HeaderCell>
            </Table.Row>
          </Table.Header>

          <Table.Body>
            <Table.Row>
              <Table.Cell />
              <Table.Cell>
                <Dropdown
                  clearable
                  search
                  selection
                  fluid
                  {...(invertLanguages ? langTransl : langWord)}
                  placeholder='Select Language'
                  options={languageOptions} />
              </Table.Cell>
              <Table.Cell textAlign='center'>
                <Button 
                  type='button'
                  onClick={() => setInvertLanguages(!invertLanguages)} 
                  icon='arrows alternate horizontal' />
              </Table.Cell>
              <Table.Cell>
                <Dropdown 
                  clearable
                  search
                  selection
                  fluid
                  {...(invertLanguages ? langWord : langTransl)}
                  placeholder='Select Language' 
                  options={languageOptions} />
              </Table.Cell>
            </Table.Row>

            { freeForm
              ? <RegularInsertionForm 
                addWord={addWord}
                removeWord={removeWord} />
              : <FreeInsertionForm 
                addMany={addMany}
                resetWordList={() => setWordList([])} /> }

            { 
              wordList.map(word =>
                <Table.Row key={word.word}>
                  <Table.Cell>
                    <Button 
                      circular
                      icon='minus'
                      onClick={() => removeWord(word.word, word.translation)}
                      type='button'/>
                  </Table.Cell>
                  <Table.Cell>{word.word}</Table.Cell>
                  <Table.Cell />
                  <Table.Cell>{word.translation}</Table.Cell>
                </Table.Row>
              )
            }
                        
            <Table.Row>
              <Table.Cell colSpan='4'>
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