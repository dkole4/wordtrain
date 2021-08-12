import React from 'react'
import { useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { 
  Container, Table, Dropdown, Button, Input, Segment, Grid, Divider, Header 
} from 'semantic-ui-react'
import PropTypes from 'prop-types'

import { setErrorMessage, setNotificationMessage } from '../reducer-notification'
import { updateScore } from '../reducer-word'
import { wordService } from '../services'
import { useField } from './WordApp'
import { languageOptions } from './WordList'


const getRandomValue = (id) => 
  Math.floor(Math.random() * id)

const getRandomizedWords = (words) =>
  words.sort((a, b) => getRandomValue(a.id) - getRandomValue(b.id))

export const TrainingView = () => {
  const [words, setWords] = useState([])
  const [wordLang, resetWordLang] = useField('dropdown')
  const [transLang, resetTransLang] = useField('dropdown')
  const [size, setSize] = useState(0)
  const user = useSelector(state => state.loggedUser)
  const dispatch = useDispatch()
  
  const getTrainingWords = async () => {
    if (size > 0 && size < 30) {
      const response = await wordService.getTrainWords(
        user, wordLang.value, transLang.value, size
      )
      const words = response.data

      if (words.length === 0)
        dispatch(setErrorMessage('No words were found.'))
      else
        setWords(words)
      setSize(0)
      resetWordLang()
      resetTransLang()
    } else {
      dispatch(setErrorMessage('Size must be between 0 and 30'))
    }
  }

  const updateSize = (e) => {
    if (e.target.value !== '' && !isNaN(e.target.value)) {
      const number = Number(e.target.value)
      if (number) {
        if (number < 1 && number >= 30)
          dispatch(setErrorMessage('Size must be between 0 and 30'))
        else
          setSize(number)
      }
      else
        dispatch(setErrorMessage('Entered size is not valid'))
    }
  }

  return (
    <Segment>
      { words.length === 0 &&
        <Table>
          <Table.Header>
            <Table.Row>
              <Table.HeaderCell>
                Translation language
              </Table.HeaderCell>
              <Table.HeaderCell>
                Training language
              </Table.HeaderCell>
              <Table.HeaderCell>
                Number of words
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
                <Input fluid placeholder='Select size' onChange={updateSize} />
              </Table.HeaderCell>
              <Table.HeaderCell>
                <Button positive onClick={getTrainingWords}>Start</Button>
              </Table.HeaderCell>
            </Table.Row>
          </Table.Header>
        </Table>
      }
      { words.length > 0 && <TrainingModule words={words} setWords={setWords} /> }
    </Segment>
  )
}

export const TrainingModule = ({ words, setWords }) => {
  const [current, setCurrent] = useState(0)
  const [score, setScore] = useState(0)
  const [changes, setChanges] = useState([])
  const [answer, resetAnswer] = useField('text')
  const [shuffled, setShuffled] = useState(getRandomizedWords([...words]))
  
  const user = useSelector(state => state.loggedUser)
  
  const dispatch = useDispatch()

  const handleKeyPress = (e) => {
    if (e.charCode === 13) {
      e.preventDefault()
      checkAnswer()
    }
  }

  const checkAnswer = () => {
    const correctAnswer = shuffled[current].translation.toLowerCase()
    
    if (correctAnswer === answer.value.toLowerCase()) {
      dispatch(
        setNotificationMessage(
          `Correct! Your score: ${score+1} / ${current+1}`
        )
      )
      
      setScore(score + 1)
      setChanges(changes.concat({wordId: shuffled[current].id, score:1, tries:1}))
    } else {
      dispatch(
        setErrorMessage(
          `Wrong. Correct answer: ${shuffled[current].translation}`
        )
      )
      
      setChanges(
        changes.concat({wordId: shuffled[current].id, score:0, tries:1})
      )
    }
    resetAnswer()
    setCurrent(current + 1)
  }

  const finishRound = () => {
    setShuffled([])
    dispatch(updateScore(user, changes))
    setWords([])
  }

  if (current >= shuffled.length) {
    return (
      <Container>
        <Header>Your score: {score} / {words.length}</Header>
        <Button positive onClick={finishRound}>Finish session</Button>
      </Container>
    )
  }

  return (
    <Container>
      <Segment placeholder>
        <Grid columns={2} stackable textAlign='center'>
          <Divider vertical />

          <Grid.Row>
            <Grid.Column>
              <Header as='h1'>
                {shuffled[current].word}
              </Header>
            </Grid.Column>

            <Grid.Column>
              <Input 
                onKeyPress={handleKeyPress}
                fluid
                {...answer}
                placeholder='Write the translation...' />
            </Grid.Column>
          </Grid.Row>
          
          <Grid.Row>
            <Grid.Column>
              <Header disabled>
                {shuffled[current].lang_word}
              </Header>
            </Grid.Column>
            
            <Grid.Column>
              <Header disabled>
                {shuffled[current].lang_translation}
              </Header>
            </Grid.Column>
          </Grid.Row>
        </Grid>
      </Segment>
    </Container>
  )
}

TrainingModule.propTypes = {
  words: PropTypes.array.isRequired,
  setWords: PropTypes.func.isRequired
}