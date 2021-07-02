from marshmallow import Schema, fields
from opendc.models.model import Model
from opendc.models.portfolio import Portfolio


class SimulationSchema(Schema):
    """
    Simulation details.
    """
    state = fields.String()


class TraceSchema(Schema):
    """
    Schema for specifying the trace of a scenario.
    """
    traceId = fields.String()
    loadSamplingFraction = fields.Float()


class TopologySchema(Schema):
    """
    Schema for topology specification for a scenario.
    """
    topologyId = fields.String()


class OperationalSchema(Schema):
    """
    Schema for the operational phenomena for a scenario.
    """
    failuresEnabled = fields.Boolean()
    performanceInterferenceEnabled = fields.Boolean()
    schedulerName = fields.String()


class ResultSchema(Schema):
    """
    Schema representing the simulation results.
    """
    max_num_deployed_images = fields.List(fields.Number())
    max_cpu_demand = fields.List(fields.Number())
    max_cpu_usage = fields.List(fields.Number())
    mean_num_deployed_images = fields.List(fields.Number())
    total_failure_slices = fields.List(fields.Number())
    total_failure_vm_slices = fields.List(fields.Number())
    total_granted_burst = fields.List(fields.Number())
    total_interfered_burst = fields.List(fields.Number())
    total_overcommitted_burst = fields.List(fields.Number())
    total_power_draw = fields.List(fields.Number())
    total_requested_burst = fields.List(fields.Number())
    total_vms_failed = fields.List(fields.Number())
    total_vms_finished = fields.List(fields.Number())
    total_vms_queued = fields.List(fields.Number())
    total_vms_submitted = fields.List(fields.Number())


class ScenarioSchema(Schema):
    """
    Schema representing a scenario.
    """
    _id = fields.String(dump_only=True)
    portfolioId = fields.String()
    name = fields.String(required=True)
    simulation = fields.Nested(SimulationSchema)
    trace = fields.Nested(TraceSchema)
    topology = fields.Nested(TopologySchema)
    operational = fields.Nested(OperationalSchema)
    results = fields.Nested(ResultSchema, dump_only=True)


class Scenario(Model):
    """Model representing a Scenario."""

    collection_name = 'scenarios'

    def check_user_access(self, user_id, edit_access):
        """Raises an error if the user with given [user_id] has insufficient access.

        Checks access on the parent project.

        :param user_id: The User ID of the user.
        :param edit_access: True when edit access should be checked, otherwise view access.
        """
        portfolio = Portfolio.from_id(self.obj['portfolioId'])
        portfolio.check_user_access(user_id, edit_access)
