package forex

package object programs {

  type RatesProgram[F[_]] = rates.RatesAlgebra[F]
  final val RatesProgram = rates.Program
}
