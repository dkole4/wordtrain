import React, { useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { 
  Table, Dropdown, Button, Input, Segment
} from 'semantic-ui-react'

import { setErrorMessage } from 'store/actions/notification-actions'
import { wordService } from 'services/word-service'
import { useField } from 'utils/useField'
import { languageOptions } from '../WordList'
import { TrainingWindow } from './TrainingWindow'


export const TrainingView = () => {
  const [words, setWords] = useState([])
  const [wordLang, resetWordLang] = useField('dropdown')
  const [transLang, resetTransLang] = useField('dropdown')
  const [invertLanguages, setInvertLanguages] = useState(false)
  const [size, setSize] = useState(0)
  const user = useSelector(state => state.loggedUser)
  const dispatch = useDispatch()

  const resetModule = () => {
    setSize(0)
    resetWordLang()
    resetTransLang()
    setWords([])
    setInvertLanguages(false)
  }
  
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
              <Table.HeaderCell width={3}>
                Translation language
              </Table.HeaderCell>
              <Table.HeaderCell width={1}/>
              <Table.HeaderCell width={3}>
                Training language
              </Table.HeaderCell>
              <Table.HeaderCell width={2}>
                Number of words
              </Table.HeaderCell>
              <Table.HeaderCell width={2}/>
            </Table.Row>

            <Table.Row>
              <Table.HeaderCell>
                <Dropdown 
                  clearable
                  selection 
                  fluid 
                  { ...(!invertLanguages ? wordLang : transLang) }
                  placeholder='Select Language' 
                  options={languageOptions} />
              </Table.HeaderCell>
              <Table.HeaderCell>
                <Button 
                  onClick={() => setInvertLanguages(!invertLanguages)} 
                  icon='arrows alternate horizontal' />
              </Table.HeaderCell>
              <Table.HeaderCell>
                <Dropdown 
                  clearable
                  selection 
                  fluid 
                  { ...(!invertLanguages ? transLang : wordLang) }
                  placeholder='Select Language'
                  options={languageOptions} />
              </Table.HeaderCell>
              <Table.HeaderCell>
                <Input fluid placeholder='Select size' onChange={updateSize} />
              </Table.HeaderCell>
              <Table.HeaderCell>
                <Button fluid positive onClick={getTrainingWords}>
                  Start
                </Button>
              </Table.HeaderCell>
            </Table.Row>
          </Table.Header>
        </Table>
      }
      { words.length > 0 && 
        <TrainingWindow 
          words={words} 
          resetModule={resetModule} 
          trainingLanguage={ invertLanguages ? wordLang.value : transLang.value } /> 
      }
    </Segment>
  )
}
