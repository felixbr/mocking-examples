import cats.effect.IO
import org.mockito.Matchers.any
import org.mockito.Mockito._
import org.scalatest.Outcome
import org.scalatest.wordspec.FixtureAnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

/**
  * This is similar to the ManualMockSpec but it uses Mockito, which takes care of recording and verifying interactions
  */
class MockitoMockSpec extends FixtureAnyWordSpec with IOSupport with MockitoSugar {

  final protected class FixtureParam {
    val externalServiceMock: ExternalService = mock[ExternalService](RETURNS_SMART_NULLS)

    val component = new ComponentUnderTestImpl(externalServiceMock)
  }

  "ComponentUnderTest" must {
    "call externalService with 'Important Message'" in { f =>
      when(f.externalServiceMock.sendMessage(any())).thenReturn(IO.unit)

      f.component.doStuff.unsafeRunSync()

      verify(f.externalServiceMock, times(1)).sendMessage("Important Message")
      verifyNoMoreInteractions(f.externalServiceMock)
    }
  }

  override protected def withFixture(test: OneArgTest): Outcome = withFixture(test.toNoArgTest(new FixtureParam))
}
