import React from 'react'
import PropTypes from 'prop-types'
import { useState } from 'react'
import { useSelector, useDispatch } from 'react-redux'
import { useField } from './WordApp'
import { setErrorMessage, setNotificationMessage } from '../reducer-notification'
import { updateWord, deleteWord, createWords } from '../reducer-word'

import {
  Container,
  Segment,
  Table,
  Button,
  Input,
  Header,
  Form,
  Dropdown
} from 'semantic-ui-react'


export const languageOptions = [
  {
    key: 'russian',
    text: 'russian',
    value: 'russian'
  },
  {
    key: 'english',
    text: 'english',
    value: 'english'
  },
  {
    key: 'finnish',
    text: 'finnish',
    value: 'finnish'
  }
]

export const WordList = () => {
  const [formVisibilty, setVisibility] = useState(false)

  const words = useSelector(state => state.words)

  const showForm = () =>
    setVisibility(!formVisibilty)

  return (
    <Container>
      <Segment>
        { formVisibilty && <><AddWordForm /><br /></> }
        <Button fluid onClick={showForm}>
          { formVisibilty
            ? 'Hide'
            : 'Add words' }
        </Button>
      </Segment>
      <Table>
        <Table.Header>
          <Table.Row>
            <Table.HeaderCell>Word</Table.HeaderCell>
            <Table.HeaderCell>Translation</Table.HeaderCell>
            <Table.HeaderCell>Languages</Table.HeaderCell>
            <Table.HeaderCell>Score</Table.HeaderCell>
            <Table.HeaderCell />
            <Table.HeaderCell />
          </Table.Row>
        </Table.Header>
        <Table.Body>
          { words.map(word => (
            <WordPair key={word.id} wordPair={word} />
          )) }
        </Table.Body>
      </Table>
    </Container>
  )
}

export const WordPair = ({ wordPair }) => {
  const [change, setChange] = useState(false)
  const [word] = useField('text', wordPair.word, false)
  const [transl] = useField('text', wordPair.translation, false)

  const dispatch = useDispatch()
  const user = useSelector(state => state.loggedUser)
  
  const switchChange = () => {
    setChange(!change)
  }

  const submitChange = () => {
    dispatch(
      updateWord(
        wordPair.id, 
        user,
        {
          word: word.value,
          translation: transl.value
        }
      )
    )
    switchChange()
    dispatch(
      setNotificationMessage(
        `The word pair ${word.value}/${transl.value} was updated.`
      )
    )
  }

  const deleteWordPair = () => {
    dispatch(deleteWord(user, wordPair.id))
  }

  return (
    <Table.Row>
      <Table.Cell>
        { change                  
          ? <Input { ...word } /> 
          : wordPair.word }
      </Table.Cell>
      <Table.Cell>
        { change
          ? <Input { ...transl } />
          : wordPair.translation }
      </Table.Cell>
      <Table.Cell>
        { wordPair.lang_word} / { wordPair.lang_translation }
      </Table.Cell>
      <Table.Cell> 
        { wordPair.tries 
          ? `${Math.round(wordPair.score / wordPair.tries * 100)} %`
          : 'was not used'
        } 
      </Table.Cell>
      <Table.Cell collapsing textAlign='right'>
        <Button onClick={switchChange}>{ change ? 'Hide' : 'Change' }</Button>
      </Table.Cell>
      <Table.Cell collapsing textAlign='right'>
        <Button positive={change} negative={!change} onClick={ change ? submitChange : deleteWordPair }>
          { change ? 'Submit' : 'Delete' }
        </Button>
      </Table.Cell>
    </Table.Row>
  )
}

WordPair.propTypes = {
  wordPair: PropTypes.object.isRequired
}

export const AddWordForm = () => {
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
        transl: transl.value,
        lang_t: translLang.value,
        lang_w: wordLang.value
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